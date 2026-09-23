package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponLifecycleCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageValidityType;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class CouponIssueDomain {

    public enum Issuability {
        ISSUABLE,
        POLICY_INACTIVE,
        ALREADY_ISSUED,
        STOCK_EXHAUSTED,
        OUT_OF_PERIOD
    }

    private static final ZoneId ZONE = ZoneOffset.UTC;

    private final CouponPolicyStatus status;
    private final CouponIssueCondition issueCondition;
    private final CouponUsageCondition usageCondition;
    private final CouponLifecycleCondition lifecycleCondition;

    private CouponIssueDomain(
        final CouponPolicyStatus status,
        final CouponIssueCondition issueCondition,
        final CouponUsageCondition usageCondition,
        final CouponLifecycleCondition lifecycleCondition
    ) {
        this.status = status;
        this.issueCondition = issueCondition;
        this.usageCondition = usageCondition;
        this.lifecycleCondition = lifecycleCondition;
    }

    public static CouponIssueDomain of(final CouponPolicyDocument policy) {
        return new CouponIssueDomain(
            policy.getStatus(),
            policy.getIssueCondition(),
            policy.getUsageCondition(),
            policy.getLifecycleCondition()
        );
    }

    public static CouponIssueDomain of(final CouponPolicyCache policy) {
        return new CouponIssueDomain(
            policy.status(),
            policy.issueCondition(),
            policy.usageCondition(),
            policy.lifecycleCondition()
        );
    }

    public Issuability issuabilityAt(
        final Instant now,
        final boolean alreadyIssued,
        final long issuedCount
    ) {
        if (status == null || status.isNotActive()) {
            return Issuability.POLICY_INACTIVE;
        }
        if (alreadyIssued) {
            return Issuability.ALREADY_ISSUED;
        }
        if (hasNoStockLeftFor(issuedCount)) {
            return Issuability.STOCK_EXHAUSTED;
        }
        if (!isOpenAt(now)) {
            return Issuability.OUT_OF_PERIOD;
        }
        return Issuability.ISSUABLE;
    }

    public boolean isOpenAt(final Instant now) {
        final Instant startAt = issueCondition.getStartAt();
        final Instant endAt = issueCondition.getEndAt();
        if (startAt != null && now.isBefore(startAt)) {
            return false;
        }
        if (endAt != null && now.isAfter(endAt)) {
            return false;
        }
        final List<DayOfWeek> openDaysOfWeek = issueCondition.getAvailableDaysOfWeek();
        if (!openDaysOfWeek.isEmpty() && !openDaysOfWeek.contains(now.atZone(ZONE).getDayOfWeek())) {
            return false;
        }
        final List<Integer> openHours = issueCondition.getHours();
        return openHours.isEmpty() || openHours.contains(now.atZone(ZONE).getHour());
    }

    public String issueKeyFor(final String policyId, final String userId, final Instant issuedAt) {
        final String perUser = policyId + ":" + userId;
        return switch (issueCondition.getFrequency()) {
            case ONCE_PER_USER -> perUser;
            case ONCE_PER_DAY -> perUser + ":" + LocalDate.from(issuedAt.atZone(ZONE));
            case ONCE_PER_MONTH -> perUser + ":" + YearMonth.from(issuedAt.atZone(ZONE));
            case ONCE_PER_YEAR -> perUser + ":" + issuedAt.atZone(ZONE).getYear();
        };
    }

    public Instant expiresAtFrom(final Instant issuedAt) {
        final CouponUsageValidityType validityType = usageCondition.getValidityType();
        if (validityType == null) {
            return null;
        }
        if (validityType == CouponUsageValidityType.FIXED_PERIOD) {
            return usageCondition.getEndAt();
        }
        final Integer daysAfterIssue = usageCondition.getDaysAfterIssue();
        if (daysAfterIssue == null) {
            return null;
        }
        return issuedAt.plus(daysAfterIssue, ChronoUnit.DAYS);
    }

    public boolean isRecoverableAfterUse() {
        return lifecycleCondition != null
            && (lifecycleCondition.isReclaimable() || lifecycleCondition.isReclaimableOnPaymentCancel());
    }

    public boolean isLastIssue(final long issuedCount) {
        final Long stockQuantity = issueCondition.getStockQuantity();
        return stockQuantity != null && issuedCount == stockQuantity;
    }

    public Long stockQuantity() {
        return issueCondition.getStockQuantity();
    }

    private boolean hasNoStockLeftFor(final long issuedCount) {
        final Long stockQuantity = issueCondition.getStockQuantity();
        return stockQuantity != null && issuedCount >= stockQuantity;
    }
}

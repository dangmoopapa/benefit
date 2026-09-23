package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.data.entity.point.policy.PointPolicyStatus;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointIssueCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointLifecycleCondition;
import im.dangmoo.benefit.data.entity.point.transaction.PointTransactionType;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.YearMonth;
import java.util.List;

public final class PointIssueDomain {

    public enum Issuability {
        ISSUABLE,
        POLICY_INACTIVE,
        ALREADY_GRANTED,
        STOCK_EXHAUSTED,
        OUT_OF_PERIOD
    }

    public enum Reclaimability {
        RECLAIMABLE,
        POLICY_NOT_RECLAIMABLE,
        INVALID_AMOUNT,
        INSUFFICIENT_BALANCE
    }

    private static final ZoneId ZONE = ZoneOffset.UTC;

    private final PointPolicyStatus status;
    private final PointIssueCondition issueCondition;
    private final PointLifecycleCondition lifecycleCondition;

    private PointIssueDomain(
        final PointPolicyStatus status,
        final PointIssueCondition issueCondition,
        final PointLifecycleCondition lifecycleCondition
    ) {
        this.status = status;
        this.issueCondition = issueCondition;
        this.lifecycleCondition = lifecycleCondition;
    }

    public static PointIssueDomain of(final PointPolicyDocument policy) {
        return new PointIssueDomain(
            policy.getStatus(),
            policy.getIssueCondition(),
            policy.getLifecycleCondition()
        );
    }

    public Issuability issuabilityAt(
        final Instant now,
        final boolean alreadyGranted,
        final long grantedCount
    ) {
        if (status == null || status.isNotActive()) {
            return Issuability.POLICY_INACTIVE;
        }
        if (alreadyGranted) {
            return Issuability.ALREADY_GRANTED;
        }
        if (hasNoStockLeftFor(grantedCount)) {
            return Issuability.STOCK_EXHAUSTED;
        }
        if (!isOpenAt(now)) {
            return Issuability.OUT_OF_PERIOD;
        }
        return Issuability.ISSUABLE;
    }

    public Reclaimability reclaimabilityOf(final long amount, final long availableAmount) {
        if (lifecycleCondition == null || !lifecycleCondition.isReclaimable()) {
            return Reclaimability.POLICY_NOT_RECLAIMABLE;
        }
        if (amount <= 0) {
            return Reclaimability.INVALID_AMOUNT;
        }
        if (amount > availableAmount) {
            return Reclaimability.INSUFFICIENT_BALANCE;
        }
        return Reclaimability.RECLAIMABLE;
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

    public String grantKeyFor(final String policyId, final String userId, final Instant grantedAt) {
        final String perUser = PointTransactionType.GRANT.name() + ":" + policyId + ":" + userId;
        return switch (issueCondition.getFrequency()) {
            case ONCE_PER_USER -> perUser;
            case ONCE_PER_DAY -> perUser + ":" + LocalDate.from(grantedAt.atZone(ZONE));
            case ONCE_PER_MONTH -> perUser + ":" + YearMonth.from(grantedAt.atZone(ZONE));
            case ONCE_PER_YEAR -> perUser + ":" + grantedAt.atZone(ZONE).getYear();
        };
    }

    public Long stockQuantity() {
        return issueCondition.getStockQuantity();
    }

    private boolean hasNoStockLeftFor(final long grantedCount) {
        final Long stockQuantity = issueCondition.getStockQuantity();
        return stockQuantity != null && grantedCount >= stockQuantity;
    }
}

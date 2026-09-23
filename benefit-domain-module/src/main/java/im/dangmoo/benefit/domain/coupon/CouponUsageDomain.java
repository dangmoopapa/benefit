package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageValidityType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class CouponUsageDomain {

    private final CouponUsageValidityType validityType;
    private final Instant startAt;
    private final Instant endAt;
    private final Integer daysAfterIssue;
    private final Long stockQuantity;
    private final BigDecimal minPaymentAmount;

    private CouponUsageDomain(
        final CouponUsageValidityType validityType,
        final Instant startAt,
        final Instant endAt,
        final Integer daysAfterIssue,
        final Long stockQuantity,
        final BigDecimal minPaymentAmount
    ) {
        this.validityType = validityType;
        this.startAt = startAt;
        this.endAt = endAt;
        this.daysAfterIssue = daysAfterIssue;
        this.stockQuantity = stockQuantity;
        this.minPaymentAmount = minPaymentAmount;
    }

    public static CouponUsageDomain of(final CouponPolicyDocument policy) {
        return of(policy.getUsageCondition());
    }

    public static CouponUsageDomain of(final CouponPolicyCache policy) {
        return of(policy.usageCondition());
    }

    static CouponUsageDomain of(final CouponUsageCondition usageCondition) {
        return new CouponUsageDomain(
            usageCondition.getValidityType(),
            usageCondition.getStartAt(),
            usageCondition.getEndAt(),
            usageCondition.getDaysAfterIssue(),
            usageCondition.getStockQuantity(),
            usageCondition.getMinPaymentAmount()
        );
    }

    public Instant expiresAtFrom(final Instant issuedAt) {
        if (validityType == null) {
            return null;
        }
        if (validityType == CouponUsageValidityType.FIXED_PERIOD) {
            return endAt;
        }
        if (daysAfterIssue == null) {
            return null;
        }
        return issuedAt.plus(daysAfterIssue, ChronoUnit.DAYS);
    }

    public boolean isUsableAt(
        final Instant now,
        final Instant issuedAt,
        final Instant expiresAt,
        final long usedCount,
        final BigDecimal paymentAmount
    ) {
        if (expiresAt != null && now.isAfter(expiresAt)) {
            return false;
        }
        if (!isInUsagePeriodAt(now, issuedAt)) {
            return false;
        }
        if (stockQuantity != null && usedCount >= stockQuantity) {
            return false;
        }
        return minPaymentAmount == null
            || (paymentAmount != null && paymentAmount.compareTo(minPaymentAmount) >= 0);
    }

    private boolean isInUsagePeriodAt(final Instant now, final Instant issuedAt) {
        if (validityType == CouponUsageValidityType.FIXED_PERIOD) {
            if (startAt != null && now.isBefore(startAt)) {
                return false;
            }
            return endAt == null || !now.isAfter(endAt);
        }
        if (validityType == CouponUsageValidityType.UNTIL_DAYS_AFTER_ISSUE
            || validityType == CouponUsageValidityType.FOR_DAYS_AFTER_ISSUE) {
            final Instant usableUntil = expiresAtFrom(issuedAt);
            return usableUntil == null || !now.isAfter(usableUntil);
        }
        return true;
    }
}

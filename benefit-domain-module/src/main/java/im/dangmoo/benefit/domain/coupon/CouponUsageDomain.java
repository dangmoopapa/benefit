package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponApplyCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageValidityType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CouponUsageDomain {

    private final CouponUsageValidityType validityType;
    private final Instant startAt;
    private final Instant endAt;
    private final Integer daysAfterIssue;
    private final Long stockQuantity;
    private final BigDecimal minPaymentAmount;
    private final CouponApplyDomain applyDomain;

    private CouponUsageDomain(
        final CouponUsageValidityType validityType,
        final Instant startAt,
        final Instant endAt,
        final Integer daysAfterIssue,
        final Long stockQuantity,
        final BigDecimal minPaymentAmount,
        final CouponApplyDomain applyDomain
    ) {
        this.validityType = validityType;
        this.startAt = startAt;
        this.endAt = endAt;
        this.daysAfterIssue = daysAfterIssue;
        this.stockQuantity = stockQuantity;
        this.minPaymentAmount = minPaymentAmount;
        this.applyDomain = applyDomain;
    }

    public static CouponUsageDomain of(
        final CouponUsageCondition usageCondition,
        final CouponApplyCondition applyCondition
    ) {
        return new CouponUsageDomain(
            usageCondition.getValidityType(),
            usageCondition.getStartAt(),
            usageCondition.getEndAt(),
            usageCondition.getDaysAfterIssue(),
            usageCondition.getStockQuantity(),
            usageCondition.getMinPaymentAmount(),
            CouponApplyDomain.of(applyCondition)
        );
    }

    public Instant resolveExpiresAt(final Instant issuedAt) {
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

    public boolean isSatisfied(
        final Instant issuedAt,
        final Instant expiresAt,
        final Instant now,
        final long usedCount,
        final BigDecimal paymentAmount,
        final String productId,
        final String categoryId,
        final String brandId,
        final String requestSegmentId
    ) {
        if (expiresAt != null && now.isAfter(expiresAt)) {
            return false;
        }
        if (validityType == CouponUsageValidityType.FIXED_PERIOD) {
            if (startAt != null && now.isBefore(startAt)) {
                return false;
            }
            if (endAt != null && now.isAfter(endAt)) {
                return false;
            }
        }
        if (validityType == CouponUsageValidityType.UNTIL_DAYS_AFTER_ISSUE
            || validityType == CouponUsageValidityType.FOR_DAYS_AFTER_ISSUE) {
            final Instant resolved = resolveExpiresAt(issuedAt);
            if (resolved != null && now.isAfter(resolved)) {
                return false;
            }
        }
        if (stockQuantity != null && usedCount >= stockQuantity) {
            return false;
        }
        if (minPaymentAmount != null) {
            if (paymentAmount == null || paymentAmount.compareTo(minPaymentAmount) < 0) {
                return false;
            }
        }
        return applyDomain.isSatisfied(productId, categoryId, brandId, requestSegmentId);
    }
}

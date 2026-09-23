package im.dangmoo.benefit.data.entity.coupon.policy.condition;

import java.math.BigDecimal;
import java.time.Instant;

public class CouponUsageCondition {

    private CouponUsageValidityType validityType;
    private Instant startAt;
    private Instant endAt;
    private Integer daysAfterIssue;
    private Long stockQuantity;
    private BigDecimal minPaymentAmount;

    private CouponUsageCondition() {
    }

    public static CouponUsageCondition create(
        final CouponUsageValidityType validityType,
        final Instant startAt,
        final Instant endAt,
        final Integer daysAfterIssue,
        final Long stockQuantity,
        final BigDecimal minPaymentAmount
    ) {
        final CouponUsageCondition condition = new CouponUsageCondition();
        condition.validityType = validityType;
        condition.startAt = startAt;
        condition.endAt = endAt;
        condition.daysAfterIssue = daysAfterIssue;
        condition.stockQuantity = stockQuantity;
        condition.minPaymentAmount = minPaymentAmount;
        return condition;
    }

    public CouponUsageValidityType getValidityType() {
        return validityType;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public Integer getDaysAfterIssue() {
        return daysAfterIssue;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public BigDecimal getMinPaymentAmount() {
        return minPaymentAmount;
    }
}

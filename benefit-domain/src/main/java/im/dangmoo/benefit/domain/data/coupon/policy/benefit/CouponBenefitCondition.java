package im.dangmoo.benefit.domain.data.coupon.policy.benefit;

import java.math.BigDecimal;

public class CouponBenefitCondition {

    private CouponBenefitType type;
    private BigDecimal value;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minPaymentAmount;

    private CouponBenefitCondition() {
    }

    public static CouponBenefitCondition create(
        final CouponBenefitType type,
        final BigDecimal value,
        final BigDecimal maxDiscountAmount,
        final BigDecimal minPaymentAmount
    ) {
        final CouponBenefitCondition entity = new CouponBenefitCondition();
        entity.type = type;
        entity.value = value;
        entity.maxDiscountAmount = maxDiscountAmount;
        entity.minPaymentAmount = minPaymentAmount;
        return entity;
    }

    public CouponBenefitType getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public BigDecimal getMinPaymentAmount() {
        return minPaymentAmount;
    }
}

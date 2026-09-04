package im.dangmoo.benefit.domain.coupon.policy.benefit;

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
        final CouponBenefitCondition document = new CouponBenefitCondition();
        document.type = type;
        document.value = value;
        document.maxDiscountAmount = maxDiscountAmount;
        document.minPaymentAmount = minPaymentAmount;
        return document;
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

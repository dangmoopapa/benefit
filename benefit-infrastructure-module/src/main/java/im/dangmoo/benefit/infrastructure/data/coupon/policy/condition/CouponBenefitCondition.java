package im.dangmoo.benefit.infrastructure.data.coupon.policy.condition;

import java.math.BigDecimal;

public class CouponBenefitCondition {

    private BigDecimal amount;
    private BigDecimal rate;
    private BigDecimal maxDiscountAmount;

    private CouponBenefitCondition() {
    }

    public static CouponBenefitCondition create(
        final BigDecimal amount,
        final BigDecimal rate,
        final BigDecimal maxDiscountAmount
    ) {
        final CouponBenefitCondition condition = new CouponBenefitCondition();
        condition.amount = amount;
        condition.rate = rate;
        condition.maxDiscountAmount = maxDiscountAmount;
        return condition;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }
}

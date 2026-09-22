package im.dangmoo.benefit.infrastructure.data.membership.policy.benefit;

import org.springframework.data.annotation.TypeAlias;

import java.math.BigDecimal;

@TypeAlias("SEASON_1")
public class Season1MembershipBenefit extends MembershipBenefit {

    private BigDecimal paymentDiscountRate;
    private BigDecimal pointCashbackRate;
    private String monthlyCouponPolicyKey;

    private Season1MembershipBenefit() {
    }

    public static Season1MembershipBenefit create(
        final BigDecimal paymentDiscountRate,
        final BigDecimal pointCashbackRate,
        final String monthlyCouponPolicyKey
    ) {
        final Season1MembershipBenefit benefit = new Season1MembershipBenefit();
        benefit.paymentDiscountRate = paymentDiscountRate;
        benefit.pointCashbackRate = pointCashbackRate;
        benefit.monthlyCouponPolicyKey = monthlyCouponPolicyKey;
        return benefit;
    }

    public BigDecimal getPaymentDiscountRate() {
        return paymentDiscountRate;
    }

    public BigDecimal getPointCashbackRate() {
        return pointCashbackRate;
    }

    public String getMonthlyCouponPolicyKey() {
        return monthlyCouponPolicyKey;
    }
}

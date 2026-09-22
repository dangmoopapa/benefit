package im.dangmoo.benefit.infrastructure.data.membership.policy.benefit;

import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitApplied;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;

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

    @Override
    public MembershipBenefitApplied apply(final BigDecimal paymentAmount, final String categoryId) {
        return MembershipBenefitApplied.of(
            rateOf(paymentAmount, paymentDiscountRate),
            rateOf(paymentAmount, pointCashbackRate),
            false,
            false
        );
    }

    @Override
    public Optional<String> monthlyCouponPolicyKey() {
        return Optional.ofNullable(monthlyCouponPolicyKey)
            .filter(StringUtils::hasText);
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

package im.dangmoo.benefit.domain.membership;

import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitApplied;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season1MembershipBenefit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class Season1MembershipBenefitDomain {

    private final Season1MembershipBenefit benefit;

    private Season1MembershipBenefitDomain(final Season1MembershipBenefit benefit) {
        this.benefit = benefit;
    }

    public static Season1MembershipBenefitDomain of(final Season1MembershipBenefit benefit) {
        return new Season1MembershipBenefitDomain(benefit);
    }

    public MembershipBenefitApplied apply(final BigDecimal paymentAmount) {
        return MembershipBenefitApplied.of(
            rateOf(paymentAmount, benefit.getPaymentDiscountRate()),
            rateOf(paymentAmount, benefit.getPointCashbackRate()),
            false,
            false
        );
    }

    public Optional<String> monthlyCouponPolicyKey() {
        return Optional.ofNullable(benefit.getMonthlyCouponPolicyKey())
            .filter(key -> !key.isBlank());
    }

    private static BigDecimal rateOf(final BigDecimal amount, final BigDecimal rate) {
        if (amount == null || rate == null || rate.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(rate).setScale(0, RoundingMode.DOWN);
    }
}

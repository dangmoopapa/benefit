package im.dangmoo.benefit.domain.membership;

import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitApplied;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season2MembershipBenefit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Season2MembershipBenefitDomain {

    private final Season2MembershipBenefit benefit;

    private Season2MembershipBenefitDomain(final Season2MembershipBenefit benefit) {
        this.benefit = benefit;
    }

    public static Season2MembershipBenefitDomain of(final Season2MembershipBenefit benefit) {
        return new Season2MembershipBenefitDomain(benefit);
    }

    public MembershipBenefitApplied apply(final BigDecimal paymentAmount, final String categoryId) {
        final boolean matched = matchesCategory(categoryId, benefit.getCategoryIds());
        return MembershipBenefitApplied.of(
            matched ? rateOf(paymentAmount, benefit.getCategoryDiscountRate()) : BigDecimal.ZERO,
            rateOf(paymentAmount, benefit.getPointCashbackRate()),
            benefit.isFreeShipping(),
            benefit.isFreeDelivery()
        );
    }

    private static boolean matchesCategory(final String categoryId, final List<String> categoryIds) {
        if (categoryId == null || categoryId.isBlank() || categoryIds == null || categoryIds.isEmpty()) {
            return false;
        }
        return categoryIds.contains(categoryId);
    }

    private static BigDecimal rateOf(final BigDecimal amount, final BigDecimal rate) {
        if (amount == null || rate == null || rate.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(rate).setScale(0, RoundingMode.DOWN);
    }
}

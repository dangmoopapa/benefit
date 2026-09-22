package im.dangmoo.benefit.domain.membership;

import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitApplied;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.MembershipBenefit;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season1MembershipBenefit;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season2MembershipBenefit;

import java.math.BigDecimal;
import java.util.Optional;

public final class MembershipBenefitDomain {

    public static final class PreparingException extends RuntimeException {
    }

    private MembershipBenefitDomain() {
    }

    public static void requireReady(final MembershipSeason season, final MembershipBenefit benefit) {
        switch (season) {
            case SEASON_1 -> {
                if (!(benefit instanceof Season1MembershipBenefit)) {
                    throw new PreparingException();
                }
            }
            case SEASON_2 -> {
                if (!(benefit instanceof Season2MembershipBenefit)) {
                    throw new PreparingException();
                }
            }
        }
    }

    public static MembershipBenefitApplied apply(
        final MembershipSeason season,
        final MembershipBenefit benefit,
        final BigDecimal paymentAmount,
        final String categoryId
    ) {
        return switch (season) {
            case SEASON_1 -> {
                if (!(benefit instanceof Season1MembershipBenefit season1)) {
                    throw new PreparingException();
                }
                yield Season1MembershipBenefitDomain.of(season1).apply(paymentAmount);
            }
            case SEASON_2 -> {
                if (!(benefit instanceof Season2MembershipBenefit season2)) {
                    throw new PreparingException();
                }
                yield Season2MembershipBenefitDomain.of(season2).apply(paymentAmount, categoryId);
            }
        };
    }

    public static Optional<String> monthlyCouponPolicyKey(
        final MembershipSeason season,
        final MembershipBenefit benefit
    ) {
        return switch (season) {
            case SEASON_1 -> {
                if (!(benefit instanceof Season1MembershipBenefit season1)) {
                    throw new PreparingException();
                }
                yield Season1MembershipBenefitDomain.of(season1).monthlyCouponPolicyKey();
            }
            case SEASON_2 -> {
                if (!(benefit instanceof Season2MembershipBenefit)) {
                    throw new PreparingException();
                }
                yield Optional.empty();
            }
        };
    }
}

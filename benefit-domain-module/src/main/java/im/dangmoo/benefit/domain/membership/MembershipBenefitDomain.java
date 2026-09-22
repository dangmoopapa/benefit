package im.dangmoo.benefit.domain.membership;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.MembershipBenefit;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season1MembershipBenefit;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season2MembershipBenefit;

public final class MembershipBenefitDomain {

    public static final class PreparingException extends RuntimeException {
    }

    private MembershipBenefitDomain() {
    }

    public static void requireReady(final MembershipSeason season, final MembershipBenefit benefit) {
        final boolean ready = switch (season) {
            case SEASON_1 -> benefit instanceof Season1MembershipBenefit;
            case SEASON_2 -> benefit instanceof Season2MembershipBenefit;
        };
        if (!ready) {
            throw new PreparingException();
        }
    }
}

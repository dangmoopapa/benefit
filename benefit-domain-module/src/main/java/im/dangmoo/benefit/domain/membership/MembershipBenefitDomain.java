package im.dangmoo.benefit.domain.membership;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.MembershipBenefit;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season1MembershipBenefit;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season2MembershipBenefit;

public final class MembershipBenefitDomain {

    private final MembershipSeason season;
    private final MembershipBenefit benefit;

    private MembershipBenefitDomain(final MembershipSeason season, final MembershipBenefit benefit) {
        this.season = season;
        this.benefit = benefit;
    }

    public static MembershipBenefitDomain of(final MembershipPolicyDocument policy) {
        return of(policy.getSeason(), policy.getBenefit());
    }

    public static MembershipBenefitDomain of(
        final MembershipSeason season,
        final MembershipBenefit benefit
    ) {
        return new MembershipBenefitDomain(season, benefit);
    }

    public boolean isServiceable() {
        if (season == null || benefit == null) {
            return false;
        }
        return switch (season) {
            case SEASON_1 -> benefit instanceof Season1MembershipBenefit;
            case SEASON_2 -> benefit instanceof Season2MembershipBenefit;
        };
    }
}

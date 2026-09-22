package im.dangmoo.benefit.admin.model.membership.policy;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicy;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.MembershipBenefit;

import java.util.List;

public record MembershipPolicySearchResponse(List<Item> items) {

    public static MembershipPolicySearchResponse of(final List<MembershipPolicy> policies) {
        return new MembershipPolicySearchResponse(
            policies.stream().map(Item::of).toList()
        );
    }

    public record Item(
        String id,
        String name,
        String key,
        MembershipPolicyStatus status,
        MembershipSeason season,
        MembershipBenefit benefit
    ) {
        public static Item of(final MembershipPolicy policy) {
            return new Item(
                policy.getId(),
                policy.getName(),
                policy.getKey(),
                policy.getStatus(),
                policy.getSeason(),
                policy.getBenefit()
            );
        }
    }
}

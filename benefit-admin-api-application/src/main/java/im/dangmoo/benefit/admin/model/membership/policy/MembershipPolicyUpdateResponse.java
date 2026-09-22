package im.dangmoo.benefit.admin.model.membership.policy;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicy;

public record MembershipPolicyUpdateResponse(String id) {

    public static MembershipPolicyUpdateResponse of(final MembershipPolicy policy) {
        return new MembershipPolicyUpdateResponse(policy.getId());
    }
}

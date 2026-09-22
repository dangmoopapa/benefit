package im.dangmoo.benefit.admin.model.membership.policy;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicy;

public record MembershipPolicyCreateResponse(String id, String key) {

    public static MembershipPolicyCreateResponse of(final MembershipPolicy policy) {
        return new MembershipPolicyCreateResponse(policy.getId(), policy.getKey());
    }
}

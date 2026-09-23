package im.dangmoo.benefit.admin.model.membership.policy;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyDocument;

public record MembershipPolicyCreateResponse(String id, String key) {

    public static MembershipPolicyCreateResponse of(final MembershipPolicyDocument policy) {
        return new MembershipPolicyCreateResponse(policy.getId(), policy.getKey());
    }
}

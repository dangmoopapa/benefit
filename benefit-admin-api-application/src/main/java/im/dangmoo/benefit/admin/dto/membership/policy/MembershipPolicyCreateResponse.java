package im.dangmoo.benefit.admin.dto.membership.policy;

import im.dangmoo.benefit.data.entity.membership.policy.MembershipPolicyDocument;

public record MembershipPolicyCreateResponse(String id, String key) {

    public static MembershipPolicyCreateResponse of(final MembershipPolicyDocument policy) {
        return new MembershipPolicyCreateResponse(policy.getId(), policy.getKey());
    }
}

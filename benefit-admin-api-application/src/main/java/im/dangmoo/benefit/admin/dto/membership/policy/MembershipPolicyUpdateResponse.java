package im.dangmoo.benefit.admin.dto.membership.policy;

import im.dangmoo.benefit.data.entity.membership.policy.MembershipPolicyDocument;

public record MembershipPolicyUpdateResponse(String id) {

    public static MembershipPolicyUpdateResponse of(final MembershipPolicyDocument policy) {
        return new MembershipPolicyUpdateResponse(policy.getId());
    }
}

package im.dangmoo.benefit.admin.model.membership.policy;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyDocument;

public record MembershipPolicyUpdateResponse(String id) {

    public static MembershipPolicyUpdateResponse of(final MembershipPolicyDocument policy) {
        return new MembershipPolicyUpdateResponse(policy.getId());
    }
}

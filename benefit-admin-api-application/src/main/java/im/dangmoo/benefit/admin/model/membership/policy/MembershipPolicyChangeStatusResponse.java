package im.dangmoo.benefit.admin.model.membership.policy;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyStatus;

public record MembershipPolicyChangeStatusResponse(String id, MembershipPolicyStatus status) {

    public static MembershipPolicyChangeStatusResponse of(final MembershipPolicyDocument policy) {
        return new MembershipPolicyChangeStatusResponse(policy.getId(), policy.getStatus());
    }
}

package im.dangmoo.benefit.admin.dto.membership.policy;

import im.dangmoo.benefit.data.entity.membership.policy.MembershipPolicyDocument;
import im.dangmoo.benefit.data.entity.membership.policy.MembershipPolicyStatus;

public record MembershipPolicyChangeStatusResponse(String id, MembershipPolicyStatus status) {

    public static MembershipPolicyChangeStatusResponse of(final MembershipPolicyDocument policy) {
        return new MembershipPolicyChangeStatusResponse(policy.getId(), policy.getStatus());
    }
}

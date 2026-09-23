package im.dangmoo.benefit.admin.dto.membership.policy;

import im.dangmoo.benefit.data.entity.membership.policy.MembershipPolicyStatus;

public record MembershipPolicyChangeStatusRequest(MembershipPolicyStatus status) {
}

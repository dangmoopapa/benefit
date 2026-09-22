package im.dangmoo.benefit.admin.model.membership.policy;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyStatus;

public record MembershipPolicyChangeStatusRequest(MembershipPolicyStatus status) {
}

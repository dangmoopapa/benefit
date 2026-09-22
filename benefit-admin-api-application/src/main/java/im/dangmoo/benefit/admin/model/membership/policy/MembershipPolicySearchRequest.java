package im.dangmoo.benefit.admin.model.membership.policy;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;

public record MembershipPolicySearchRequest(
    String key,
    String name,
    MembershipPolicyStatus status,
    MembershipSeason season
) {
}

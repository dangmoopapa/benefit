package im.dangmoo.benefit.admin.dto.membership.policy;

import im.dangmoo.benefit.data.entity.membership.policy.MembershipPolicyStatus;
import im.dangmoo.benefit.data.entity.membership.policy.MembershipSeason;

public record MembershipPolicySearchRequest(
    String key,
    String name,
    MembershipPolicyStatus status,
    MembershipSeason season
) {
}

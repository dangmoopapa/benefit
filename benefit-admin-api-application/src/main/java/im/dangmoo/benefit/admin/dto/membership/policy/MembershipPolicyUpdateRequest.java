package im.dangmoo.benefit.admin.dto.membership.policy;

import im.dangmoo.benefit.data.entity.membership.policy.MembershipSeason;
import im.dangmoo.benefit.data.entity.membership.policy.benefit.MembershipBenefit;
import im.dangmoo.benefit.data.entity.membership.policy.condition.MembershipAccountCondition;

public record MembershipPolicyUpdateRequest(
    String name,
    String description,
    MembershipSeason season,
    MembershipBenefit benefit,
    AccountCondition accountCondition
) {

    public record AccountCondition(String accountKey) {
        public MembershipAccountCondition toDocument() {
            return MembershipAccountCondition.create(accountKey);
        }
    }
}

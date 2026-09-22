package im.dangmoo.benefit.admin.model.membership.policy;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicy;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.MembershipBenefit;
import im.dangmoo.benefit.infrastructure.data.membership.policy.condition.MembershipAccountCondition;

public record MembershipPolicyCreateRequest(
    String name,
    String description,
    String key,
    MembershipSeason season,
    MembershipBenefit benefit,
    AccountCondition accountCondition
) {

    public MembershipPolicy toDocument(final String createdBy) {
        return MembershipPolicy.create(
            name,
            description,
            key,
            season,
            benefit,
            accountCondition.toDocument(),
            createdBy
        );
    }

    public record AccountCondition(String accountKey) {
        public MembershipAccountCondition toDocument() {
            return MembershipAccountCondition.create(accountKey);
        }
    }
}

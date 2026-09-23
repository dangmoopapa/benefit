package im.dangmoo.benefit.admin.dto.membership.policy;

import im.dangmoo.benefit.data.entity.membership.policy.MembershipPolicyDocument;
import im.dangmoo.benefit.data.entity.membership.policy.MembershipSeason;
import im.dangmoo.benefit.data.entity.membership.policy.benefit.MembershipBenefit;
import im.dangmoo.benefit.data.entity.membership.policy.condition.MembershipAccountCondition;

public record MembershipPolicyCreateRequest(
    String name,
    String description,
    String key,
    MembershipSeason season,
    MembershipBenefit benefit,
    AccountCondition accountCondition
) {

    public MembershipPolicyDocument toDocument(final String createdBy) {
        return MembershipPolicyDocument.create(
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

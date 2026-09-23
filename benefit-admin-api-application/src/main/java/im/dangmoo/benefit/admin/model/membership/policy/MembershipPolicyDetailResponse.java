package im.dangmoo.benefit.admin.model.membership.policy;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.MembershipBenefit;

import java.time.Instant;

public record MembershipPolicyDetailResponse(
    String id,
    String name,
    String description,
    String key,
    MembershipPolicyStatus status,
    MembershipSeason season,
    MembershipBenefit benefit,
    String accountKey,
    Instant createdAt,
    Instant updatedAt
) {

    public static MembershipPolicyDetailResponse of(final MembershipPolicyDocument policy) {
        return new MembershipPolicyDetailResponse(
            policy.getId(),
            policy.getName(),
            policy.getDescription(),
            policy.getKey(),
            policy.getStatus(),
            policy.getSeason(),
            policy.getBenefit(),
            policy.getAccountCondition().getAccountKey(),
            policy.getCreatedAt(),
            policy.getUpdatedAt()
        );
    }
}

package im.dangmoo.benefit.data.entity.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.policy.condition.*;

import java.time.Instant;

public record CouponPolicyCache(
    String id,
    String name,
    String description,
    String key,
    CouponPolicyType type,
    CouponPolicyStatus status,
    CouponBenefitCondition benefitCondition,
    CouponIssueCondition issueCondition,
    CouponUsageCondition usageCondition,
    CouponApplyCondition applyCondition,
    CouponLifecycleCondition lifecycleCondition,
    CouponAccountCondition accountCondition,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static CouponPolicyCache of(final CouponPolicyDocument policy) {
        return new CouponPolicyCache(
            policy.getId(),
            policy.getName(),
            policy.getDescription(),
            policy.getKey(),
            policy.getType(),
            policy.getStatus(),
            policy.getBenefitCondition(),
            policy.getIssueCondition(),
            policy.getUsageCondition(),
            policy.getApplyCondition(),
            policy.getLifecycleCondition(),
            policy.getAccountCondition(),
            policy.getCreatedBy(),
            policy.getCreatedAt(),
            policy.getUpdatedBy(),
            policy.getUpdatedAt()
        );
    }
}

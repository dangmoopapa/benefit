package im.dangmoo.benefit.infrastructure.data.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.*;

import java.time.Instant;

public record CachedCouponPolicy(
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

    public static CachedCouponPolicy of(final CouponPolicy policy) {
        return new CachedCouponPolicy(
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

package im.dangmoo.benefit.domain.data.coupon.policy;

import im.dangmoo.benefit.domain.data.coupon.policy.apply.CouponApplyCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.benefit.CouponBenefitCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssueCondition;

import java.time.Instant;

public record CouponPolicyChangedEvent(
    String policyId,
    String code,
    String name,
    String platformId,
    CouponPolicyType type,
    CouponPolicyStatus status,
    CouponIssueCondition issueCondition,
    CouponBenefitCondition benefitCondition,
    CouponApplyCondition applyCondition,
    Instant occurredAt
) {
    public static CouponPolicyChangedEvent of(final CouponPolicy policy) {
        return new CouponPolicyChangedEvent(
            policy.getId(),
            policy.getCode(),
            policy.getName(),
            policy.getPlatformId(),
            policy.getType(),
            policy.getStatus(),
            policy.getIssueCondition(),
            policy.getBenefitCondition(),
            policy.getApplyCondition(),
            Instant.now()
        );
    }
}

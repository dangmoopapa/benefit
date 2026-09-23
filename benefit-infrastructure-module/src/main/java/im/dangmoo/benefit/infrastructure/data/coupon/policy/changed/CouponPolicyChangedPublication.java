package im.dangmoo.benefit.infrastructure.data.coupon.policy.changed;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.*;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponApplyCondition;

import java.time.Instant;
import java.util.List;

public record CouponPolicyChangedPublication(
    String policyId,
    String key,
    String name,
    CouponPolicyType type,
    CouponPolicyStatus status,
    CouponPolicyChangedType changeType,
    List<String> productIds,
    List<String> categoryIds,
    List<String> brandIds,
    Instant occurredAt
) {

    public static CouponPolicyChangedPublication ofCreated(final CouponPolicyDocument policy) {
        return of(policy, CouponPolicyChangedType.CREATED);
    }

    public static CouponPolicyChangedPublication ofStatusChanged(final CouponPolicyDocument policy) {
        return of(policy, CouponPolicyChangedType.STATUS_CHANGED);
    }

    public static CouponPolicyChangedPublication ofExhausted(final CouponPolicyDocument policy) {
        return of(policy, CouponPolicyChangedType.EXHAUSTED);
    }

    public static CouponPolicyChangedPublication ofExhausted(final CouponPolicyCache policy) {
        return of(policy, CouponPolicyChangedType.EXHAUSTED);
    }

    private static CouponPolicyChangedPublication of(
        final CouponPolicyDocument policy,
        final CouponPolicyChangedType changeType
    ) {
        final CouponApplyCondition apply = policy.getApplyCondition();
        return new CouponPolicyChangedPublication(
            policy.getId(),
            policy.getKey(),
            policy.getName(),
            policy.getType(),
            policy.getStatus(),
            changeType,
            apply == null ? List.of() : apply.getProductIds(),
            apply == null ? List.of() : apply.getCategoryIds(),
            apply == null ? List.of() : apply.getBrandIds(),
            Instant.now()
        );
    }

    private static CouponPolicyChangedPublication of(
        final CouponPolicyCache policy,
        final CouponPolicyChangedType changeType
    ) {
        final CouponApplyCondition apply = policy.applyCondition();
        return new CouponPolicyChangedPublication(
            policy.id(),
            policy.key(),
            policy.name(),
            policy.type(),
            policy.status(),
            changeType,
            apply == null ? List.of() : apply.getProductIds(),
            apply == null ? List.of() : apply.getCategoryIds(),
            apply == null ? List.of() : apply.getBrandIds(),
            Instant.now()
        );
    }
}

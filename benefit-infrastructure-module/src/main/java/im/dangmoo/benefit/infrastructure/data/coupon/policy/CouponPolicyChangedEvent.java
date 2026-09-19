package im.dangmoo.benefit.infrastructure.data.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponApplyCondition;

import java.time.Instant;
import java.util.List;

public record CouponPolicyChangedEvent(
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

    public static CouponPolicyChangedEvent ofCreated(final CouponPolicy policy) {
        return of(policy, CouponPolicyChangedType.CREATED);
    }

    public static CouponPolicyChangedEvent ofStatusChanged(final CouponPolicy policy) {
        return of(policy, CouponPolicyChangedType.STATUS_CHANGED);
    }

    public static CouponPolicyChangedEvent ofExhausted(final CouponPolicy policy) {
        return of(policy, CouponPolicyChangedType.EXHAUSTED);
    }

    public static CouponPolicyChangedEvent ofExhausted(final CachedCouponPolicy policy) {
        return of(policy, CouponPolicyChangedType.EXHAUSTED);
    }

    private static CouponPolicyChangedEvent of(
        final CouponPolicy policy,
        final CouponPolicyChangedType changeType
    ) {
        final CouponApplyCondition apply = policy.getApplyCondition();
        return new CouponPolicyChangedEvent(
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

    private static CouponPolicyChangedEvent of(
        final CachedCouponPolicy policy,
        final CouponPolicyChangedType changeType
    ) {
        final CouponApplyCondition apply = policy.applyCondition();
        return new CouponPolicyChangedEvent(
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

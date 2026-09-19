package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyType;

import java.time.Instant;
import java.util.List;

public record CouponPolicySearchResponse(List<Item> items) {

    public static CouponPolicySearchResponse of(final List<CouponPolicy> policies) {
        return new CouponPolicySearchResponse(policies.stream().map(Item::of).toList());
    }

    public record Item(
        String id,
        String key,
        String name,
        CouponPolicyType type,
        CouponPolicyStatus status,
        Instant createdAt,
        Instant updatedAt
    ) {

        public static Item of(final CouponPolicy policy) {
            return new Item(
                policy.getId(),
                policy.getKey(),
                policy.getName(),
                policy.getType(),
                policy.getStatus(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
            );
        }
    }
}

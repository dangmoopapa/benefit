package im.dangmoo.benefit.admin.dto.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyType;

import java.time.Instant;
import java.util.List;

public record CouponPolicySearchResponse(List<Item> items) {

    public static CouponPolicySearchResponse of(final List<CouponPolicyDocument> policies) {
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

        public static Item of(final CouponPolicyDocument policy) {
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

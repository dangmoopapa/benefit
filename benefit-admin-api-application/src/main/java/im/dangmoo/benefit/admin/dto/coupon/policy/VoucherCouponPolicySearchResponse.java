package im.dangmoo.benefit.admin.dto.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponApplyCondition;

import java.time.Instant;
import java.util.List;

public record VoucherCouponPolicySearchResponse(List<Item> items) {

    public static VoucherCouponPolicySearchResponse of(final List<CouponPolicyDocument> policies) {
        return new VoucherCouponPolicySearchResponse(policies.stream().map(Item::of).toList());
    }

    public record Item(
        String id,
        String key,
        String name,
        CouponPolicyStatus status,
        List<String> productIds,
        List<String> brandIds,
        Instant createdAt,
        Instant updatedAt
    ) {

        public static Item of(final CouponPolicyDocument policy) {
            final CouponApplyCondition apply = policy.getApplyCondition();
            return new Item(
                policy.getId(),
                policy.getKey(),
                policy.getName(),
                policy.getStatus(),
                apply == null ? List.of() : apply.getProductIds(),
                apply == null ? List.of() : apply.getBrandIds(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
            );
        }
    }
}

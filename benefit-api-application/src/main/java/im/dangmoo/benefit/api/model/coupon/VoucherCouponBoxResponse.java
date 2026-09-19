package im.dangmoo.benefit.api.model.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;

import java.util.List;

public record VoucherCouponBoxResponse(List<Item> items) {

    public static VoucherCouponBoxResponse of(final List<Item> items) {
        return new VoucherCouponBoxResponse(items);
    }

    public record Item(
        String policyId,
        String policyKey,
        String policyName,
        String description
    ) {

        public static Item of(final CouponPolicy policy) {
            return new Item(
                policy.getId(),
                policy.getKey(),
                policy.getName(),
                policy.getDescription()
            );
        }
    }
}

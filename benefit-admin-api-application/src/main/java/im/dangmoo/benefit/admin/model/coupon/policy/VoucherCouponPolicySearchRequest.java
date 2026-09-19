package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyStatus;

public record VoucherCouponPolicySearchRequest(
    String productId,
    String brandId,
    CouponPolicyStatus status
) {
}

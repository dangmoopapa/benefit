package im.dangmoo.benefit.admin.dto.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyStatus;

public record VoucherCouponPolicySearchRequest(
    String productId,
    String brandId,
    CouponPolicyStatus status
) {
}

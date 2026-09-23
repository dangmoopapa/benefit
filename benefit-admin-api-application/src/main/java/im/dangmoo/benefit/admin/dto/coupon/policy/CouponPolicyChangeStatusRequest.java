package im.dangmoo.benefit.admin.dto.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyStatus;

public record CouponPolicyChangeStatusRequest(
    CouponPolicyStatus status
) {
}

package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyStatus;

public record CouponPolicyChangeStatusRequest(
    CouponPolicyStatus status
) {
}

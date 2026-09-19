package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyType;

public record CouponPolicySearchRequest(
    String key,
    String name,
    CouponPolicyType type,
    CouponPolicyStatus status
) {
}

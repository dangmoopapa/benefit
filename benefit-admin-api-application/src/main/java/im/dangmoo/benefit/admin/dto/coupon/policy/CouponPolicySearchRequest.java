package im.dangmoo.benefit.admin.dto.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyType;

public record CouponPolicySearchRequest(
    String key,
    String name,
    CouponPolicyType type,
    CouponPolicyStatus status
) {
}

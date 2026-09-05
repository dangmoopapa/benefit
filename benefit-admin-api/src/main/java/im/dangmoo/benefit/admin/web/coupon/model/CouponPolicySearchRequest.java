package im.dangmoo.benefit.admin.web.coupon.model;

import im.dangmoo.benefit.domain.coupon.document.policy.CouponPolicyStatus;
import im.dangmoo.benefit.domain.coupon.document.policy.CouponPolicyType;

public record CouponPolicySearchRequest(
    String code,
    String name,
    String platformId,
    CouponPolicyType type,
    CouponPolicyStatus status
) {
}

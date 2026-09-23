package im.dangmoo.benefit.admin.dto.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;

public record CouponPolicyChangeStatusResponse(String id) {

    public static CouponPolicyChangeStatusResponse of(final CouponPolicyDocument policy) {
        return new CouponPolicyChangeStatusResponse(policy.getId());
    }
}

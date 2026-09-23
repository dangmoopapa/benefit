package im.dangmoo.benefit.admin.dto.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;

public record CouponPolicyCreateResponse(String id) {

    public static CouponPolicyCreateResponse of(final CouponPolicyDocument policy) {
        return new CouponPolicyCreateResponse(policy.getId());
    }
}

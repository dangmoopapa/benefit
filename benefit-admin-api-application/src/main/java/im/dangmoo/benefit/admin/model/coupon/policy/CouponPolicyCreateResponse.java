package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyDocument;

public record CouponPolicyCreateResponse(String id) {

    public static CouponPolicyCreateResponse of(final CouponPolicyDocument policy) {
        return new CouponPolicyCreateResponse(policy.getId());
    }
}

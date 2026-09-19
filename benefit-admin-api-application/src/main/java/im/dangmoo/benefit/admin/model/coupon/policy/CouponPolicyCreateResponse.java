package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;

public record CouponPolicyCreateResponse(String id) {

    public static CouponPolicyCreateResponse of(final CouponPolicy policy) {
        return new CouponPolicyCreateResponse(policy.getId());
    }
}

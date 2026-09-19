package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;

public record CouponPolicyUpdateResponse(String id) {

    public static CouponPolicyUpdateResponse of(final CouponPolicy policy) {
        return new CouponPolicyUpdateResponse(policy.getId());
    }
}

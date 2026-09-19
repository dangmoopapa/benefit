package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;

public record CouponPolicyChangeStatusResponse(String id) {

    public static CouponPolicyChangeStatusResponse of(final CouponPolicy policy) {
        return new CouponPolicyChangeStatusResponse(policy.getId());
    }
}

package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyDocument;

public record CouponPolicyChangeStatusResponse(String id) {

    public static CouponPolicyChangeStatusResponse of(final CouponPolicyDocument policy) {
        return new CouponPolicyChangeStatusResponse(policy.getId());
    }
}

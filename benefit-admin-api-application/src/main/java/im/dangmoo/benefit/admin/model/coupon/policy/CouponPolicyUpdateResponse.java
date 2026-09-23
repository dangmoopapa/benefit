package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyDocument;

public record CouponPolicyUpdateResponse(String id) {

    public static CouponPolicyUpdateResponse of(final CouponPolicyDocument policy) {
        return new CouponPolicyUpdateResponse(policy.getId());
    }
}

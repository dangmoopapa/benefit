package im.dangmoo.benefit.admin.dto.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;

public record CouponPolicyUpdateResponse(String id) {

    public static CouponPolicyUpdateResponse of(final CouponPolicyDocument policy) {
        return new CouponPolicyUpdateResponse(policy.getId());
    }
}

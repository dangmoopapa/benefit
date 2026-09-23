package im.dangmoo.benefit.data.entity.coupon.policy;

public enum CouponPolicyStatus {
    DRAFT,
    ACTIVE,
    ENDED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isNotActive() {
        return !isActive();
    }
}

package im.dangmoo.benefit.infrastructure.data.coupon.policy;

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

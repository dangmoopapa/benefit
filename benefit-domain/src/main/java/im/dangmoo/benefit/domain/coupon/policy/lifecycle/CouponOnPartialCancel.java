package im.dangmoo.benefit.domain.coupon.policy.lifecycle;

public enum CouponOnPartialCancel {
    KEEP,
    RECOVER,
    RECOVER_IF_UNUSED,
    VOID
}

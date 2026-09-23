package im.dangmoo.benefit.data.entity.coupon.policy.condition;

public class CouponLifecycleCondition {

    private boolean reclaimableOnPaymentCancel;
    private boolean reclaimable;

    private CouponLifecycleCondition() {
    }

    public static CouponLifecycleCondition create(
        final boolean reclaimableOnPaymentCancel,
        final boolean reclaimable
    ) {
        final CouponLifecycleCondition condition = new CouponLifecycleCondition();
        condition.reclaimableOnPaymentCancel = reclaimableOnPaymentCancel;
        condition.reclaimable = reclaimable;
        return condition;
    }

    public boolean isReclaimableOnPaymentCancel() {
        return reclaimableOnPaymentCancel;
    }

    public boolean isReclaimable() {
        return reclaimable;
    }
}

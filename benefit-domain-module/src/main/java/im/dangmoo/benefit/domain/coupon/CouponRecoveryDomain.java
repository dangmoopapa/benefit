package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponLifecycleCondition;

public class CouponRecoveryDomain {

    private final boolean reclaimableOnPaymentCancel;
    private final boolean reclaimable;

    private CouponRecoveryDomain(final boolean reclaimableOnPaymentCancel, final boolean reclaimable) {
        this.reclaimableOnPaymentCancel = reclaimableOnPaymentCancel;
        this.reclaimable = reclaimable;
    }

    public static CouponRecoveryDomain of(final CouponLifecycleCondition condition) {
        return new CouponRecoveryDomain(
            condition.isReclaimableOnPaymentCancel(),
            condition.isReclaimable()
        );
    }

    public boolean isRecoverable() {
        return reclaimable || reclaimableOnPaymentCancel;
    }
}

package im.dangmoo.benefit.domain.data.coupon.policy.lifecycle;

public class CouponLifecycleCondition {

    private CouponOnOrderCancel onOrderCancel;
    private CouponOnPartialCancel onPartialCancel;
    private CouponOnRefund onRefund;
    private boolean reissuable;
    private CouponAccountingCondition accountingCondition;

    private CouponLifecycleCondition() {
    }

    public static CouponLifecycleCondition create(
        final CouponOnOrderCancel onOrderCancel,
        final CouponOnPartialCancel onPartialCancel,
        final CouponOnRefund onRefund,
        final boolean reissuable,
        final CouponAccountingCondition accountingCondition
    ) {
        final CouponLifecycleCondition entity = new CouponLifecycleCondition();
        entity.onOrderCancel = onOrderCancel;
        entity.onPartialCancel = onPartialCancel;
        entity.onRefund = onRefund;
        entity.reissuable = reissuable;
        entity.accountingCondition = accountingCondition;
        return entity;
    }

    public CouponOnOrderCancel getOnOrderCancel() {
        return onOrderCancel;
    }

    public CouponOnPartialCancel getOnPartialCancel() {
        return onPartialCancel;
    }

    public CouponOnRefund getOnRefund() {
        return onRefund;
    }

    public boolean isReissuable() {
        return reissuable;
    }

    public CouponAccountingCondition getAccountingCondition() {
        return accountingCondition;
    }
}

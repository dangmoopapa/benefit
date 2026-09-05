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
        final CouponLifecycleCondition document = new CouponLifecycleCondition();
        document.onOrderCancel = onOrderCancel;
        document.onPartialCancel = onPartialCancel;
        document.onRefund = onRefund;
        document.reissuable = reissuable;
        document.accountingCondition = accountingCondition;
        return document;
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

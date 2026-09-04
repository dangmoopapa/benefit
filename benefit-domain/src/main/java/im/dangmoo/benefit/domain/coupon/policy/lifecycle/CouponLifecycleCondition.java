package im.dangmoo.benefit.domain.coupon.policy.lifecycle;

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

    public boolean recoversOnOrderCancel() {
        return onOrderCancel == CouponOnOrderCancel.RECOVER;
    }

    public boolean recoversOnPartialCancel(final boolean unused) {
        return switch (onPartialCancel) {
            case RECOVER -> true;
            case RECOVER_IF_UNUSED -> unused;
            case KEEP, VOID -> false;
        };
    }

    public boolean recalculatesOnRefund() {
        return onRefund == CouponOnRefund.RECALCULATE;
    }

    public CouponAccountingCondition getAccountingCondition() {
        return accountingCondition;
    }
}

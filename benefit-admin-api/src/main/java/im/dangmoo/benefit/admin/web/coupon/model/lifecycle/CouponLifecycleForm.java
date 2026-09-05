package im.dangmoo.benefit.admin.web.coupon.model.lifecycle;

import im.dangmoo.benefit.domain.data.coupon.policy.lifecycle.CouponLifecycleCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.lifecycle.CouponOnOrderCancel;
import im.dangmoo.benefit.domain.data.coupon.policy.lifecycle.CouponOnPartialCancel;
import im.dangmoo.benefit.domain.data.coupon.policy.lifecycle.CouponOnRefund;

public record CouponLifecycleForm(
    CouponOnOrderCancel onOrderCancel,
    CouponOnPartialCancel onPartialCancel,
    CouponOnRefund onRefund,
    boolean reissuable,
    CouponAccountingForm accountingCondition
) {

    public CouponLifecycleCondition toDocument() {
        return CouponLifecycleCondition.create(
            onOrderCancel,
            onPartialCancel,
            onRefund,
            reissuable,
            accountingCondition.toDocument()
        );
    }

    public static CouponLifecycleForm of(final CouponLifecycleCondition document) {
        return new CouponLifecycleForm(
            document.getOnOrderCancel(),
            document.getOnPartialCancel(),
            document.getOnRefund(),
            document.isReissuable(),
            CouponAccountingForm.of(document.getAccountingCondition())
        );
    }
}

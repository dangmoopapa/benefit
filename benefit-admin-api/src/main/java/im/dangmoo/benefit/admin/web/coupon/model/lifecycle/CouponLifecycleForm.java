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

    public CouponLifecycleCondition toEntity() {
        return CouponLifecycleCondition.create(
            onOrderCancel,
            onPartialCancel,
            onRefund,
            reissuable,
            accountingCondition.toEntity()
        );
    }

    public static CouponLifecycleForm of(final CouponLifecycleCondition entity) {
        return new CouponLifecycleForm(
            entity.getOnOrderCancel(),
            entity.getOnPartialCancel(),
            entity.getOnRefund(),
            entity.isReissuable(),
            CouponAccountingForm.of(entity.getAccountingCondition())
        );
    }
}

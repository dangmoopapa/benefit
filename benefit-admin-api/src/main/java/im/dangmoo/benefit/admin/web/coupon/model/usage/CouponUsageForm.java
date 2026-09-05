package im.dangmoo.benefit.admin.web.coupon.model.usage;

import im.dangmoo.benefit.domain.coupon.document.policy.usage.CouponUsageCondition;
import im.dangmoo.benefit.domain.coupon.document.policy.usage.CouponUsableWeekday;

import java.util.List;

public record CouponUsageForm(
    CouponUsageValidityForm validity,
    boolean usableImmediately,
    List<CouponUsableWeekday> weekdays,
    List<CouponUsableTimeForm> timeRanges,
    CouponOrderForm orderCondition,
    CouponStackingForm stackingCondition,
    CouponUsageLimitForm limit
) {

    public CouponUsageCondition toDocument() {
        return CouponUsageCondition.create(
            validity.toDocument(),
            usableImmediately,
            weekdays,
            timeRanges.stream().map(CouponUsableTimeForm::toDocument).toList(),
            orderCondition.toDocument(),
            stackingCondition.toDocument(),
            limit.toDocument()
        );
    }

    public static CouponUsageForm of(final CouponUsageCondition document) {
        return new CouponUsageForm(
            CouponUsageValidityForm.of(document.getValidity()),
            document.isUsableImmediately(),
            document.getWeekdays(),
            document.getTimeRanges().stream().map(CouponUsableTimeForm::of).toList(),
            CouponOrderForm.of(document.getOrderCondition()),
            CouponStackingForm.of(document.getStackingCondition()),
            CouponUsageLimitForm.of(document.getLimit())
        );
    }
}

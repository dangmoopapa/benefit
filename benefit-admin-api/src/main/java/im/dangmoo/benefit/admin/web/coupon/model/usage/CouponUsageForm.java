package im.dangmoo.benefit.admin.web.coupon.model.usage;

import im.dangmoo.benefit.domain.data.coupon.policy.usage.CouponUsageCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.usage.CouponUsableWeekday;

import java.util.List;

public record CouponUsageForm(
    CouponUsageExpirationForm usageExpiration,
    boolean usableImmediately,
    List<CouponUsableWeekday> weekdays,
    List<CouponUsableTimeForm> timeRanges,
    CouponOrderForm orderCondition,
    CouponUsageLimitForm limit
) {

    public CouponUsageCondition toDocument() {
        return CouponUsageCondition.create(
            usageExpiration.toDocument(),
            usableImmediately,
            weekdays,
            timeRanges.stream().map(CouponUsableTimeForm::toDocument).toList(),
            orderCondition.toDocument(),
            limit.toDocument()
        );
    }

    public static CouponUsageForm of(final CouponUsageCondition document) {
        return new CouponUsageForm(
            CouponUsageExpirationForm.of(document.getUsageExpiration()),
            document.isUsableImmediately(),
            document.getWeekdays(),
            document.getTimeRanges().stream().map(CouponUsableTimeForm::of).toList(),
            CouponOrderForm.of(document.getOrderCondition()),
            CouponUsageLimitForm.of(document.getLimit())
        );
    }
}

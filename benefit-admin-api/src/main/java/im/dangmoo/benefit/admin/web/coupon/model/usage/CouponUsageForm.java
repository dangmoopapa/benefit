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

    public CouponUsageCondition toEntity() {
        return CouponUsageCondition.create(
            usageExpiration.toEntity(),
            usableImmediately,
            weekdays,
            timeRanges.stream().map(CouponUsableTimeForm::toEntity).toList(),
            orderCondition.toEntity(),
            limit.toEntity()
        );
    }

    public static CouponUsageForm of(final CouponUsageCondition entity) {
        return new CouponUsageForm(
            CouponUsageExpirationForm.of(entity.getUsageExpiration()),
            entity.isUsableImmediately(),
            entity.getWeekdays(),
            entity.getTimeRanges().stream().map(CouponUsableTimeForm::of).toList(),
            CouponOrderForm.of(entity.getOrderCondition()),
            CouponUsageLimitForm.of(entity.getLimit())
        );
    }
}

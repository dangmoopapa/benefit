package im.dangmoo.benefit.domain.data.coupon.policy.usage;

import java.util.List;

public class CouponUsageCondition {

    private CouponUsageExpiration usageExpiration;
    private boolean usableImmediately;
    private List<CouponUsableWeekday> weekdays;
    private List<CouponUsableTime> timeRanges;
    private CouponOrderCondition orderCondition;
    private CouponUsageLimit limit;

    private CouponUsageCondition() {
    }

    public static CouponUsageCondition create(
        final CouponUsageExpiration usageExpiration,
        final boolean usableImmediately,
        final List<CouponUsableWeekday> weekdays,
        final List<CouponUsableTime> timeRanges,
        final CouponOrderCondition orderCondition,
        final CouponUsageLimit limit
    ) {
        final CouponUsageCondition document = new CouponUsageCondition();
        document.usageExpiration = usageExpiration;
        document.usableImmediately = usableImmediately;
        document.weekdays = weekdays;
        document.timeRanges = timeRanges;
        document.orderCondition = orderCondition;
        document.limit = limit;
        return document;
    }

    public CouponUsageExpiration getUsageExpiration() {
        return usageExpiration;
    }

    public boolean isUsableImmediately() {
        return usableImmediately;
    }

    public List<CouponUsableWeekday> getWeekdays() {
        return weekdays;
    }

    public List<CouponUsableTime> getTimeRanges() {
        return timeRanges;
    }

    public CouponOrderCondition getOrderCondition() {
        return orderCondition;
    }

    public CouponUsageLimit getLimit() {
        return limit;
    }
}

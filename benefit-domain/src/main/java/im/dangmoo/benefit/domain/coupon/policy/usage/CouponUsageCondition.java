package im.dangmoo.benefit.domain.coupon.policy.usage;

import java.util.List;

public class CouponUsageCondition {

    private CouponUsageValidity validity;
    private boolean usableImmediately;
    private List<CouponUsableWeekday> weekdays;
    private List<CouponUsableTime> timeRanges;
    private CouponOrderCondition orderCondition;
    private CouponStackingCondition stackingCondition;
    private CouponUsageLimit limit;

    protected CouponUsageCondition() {
    }

    public static CouponUsageCondition create(
        final CouponUsageValidity validity,
        final boolean usableImmediately,
        final List<CouponUsableWeekday> weekdays,
        final List<CouponUsableTime> timeRanges,
        final CouponOrderCondition orderCondition,
        final CouponStackingCondition stackingCondition,
        final CouponUsageLimit limit
    ) {
        final CouponUsageCondition document = new CouponUsageCondition();
        document.validity = validity;
        document.usableImmediately = usableImmediately;
        document.weekdays = weekdays;
        document.timeRanges = timeRanges;
        document.orderCondition = orderCondition;
        document.stackingCondition = stackingCondition;
        document.limit = limit;
        return document;
    }

    public CouponUsageValidity getValidity() {
        return validity;
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

    public CouponStackingCondition getStackingCondition() {
        return stackingCondition;
    }

    public CouponUsageLimit getLimit() {
        return limit;
    }
}

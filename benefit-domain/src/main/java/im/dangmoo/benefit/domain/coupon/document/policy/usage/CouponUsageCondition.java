package im.dangmoo.benefit.domain.coupon.document.policy.usage;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class CouponUsageCondition {

    private CouponUsageValidity validity;
    private boolean usableImmediately;
    private List<CouponUsableWeekday> weekdays;
    private List<CouponUsableTime> timeRanges;
    private CouponOrderCondition orderCondition;
    private CouponStackingCondition stackingCondition;
    private CouponUsageLimit limit;

    private CouponUsageCondition() {
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

    public boolean isSatisfiedAt(final Instant issuedAt, final Instant now) {
        if (!usableImmediately && !now.isAfter(issuedAt)) {
            return false;
        }
        if (!validity.isActiveAt(issuedAt, now)) {
            return false;
        }
        final ZonedDateTime at = now.atZone(ZoneOffset.UTC);
        if (!weekdays.isEmpty() && weekdays.stream().noneMatch(weekday -> weekday.dayOfWeek == at.getDayOfWeek())) {
            return false;
        }
        if (!timeRanges.isEmpty()) {
            final LocalTime time = at.toLocalTime();
            return timeRanges.stream().anyMatch(range ->
                !time.isBefore(range.getStart()) && !time.isAfter(range.getEnd())
            );
        }
        return true;
    }
}

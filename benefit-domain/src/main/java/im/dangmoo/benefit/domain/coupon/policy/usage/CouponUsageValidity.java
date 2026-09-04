package im.dangmoo.benefit.domain.coupon.policy.usage;

import java.time.Instant;

public class CouponUsageValidity {

    private CouponUsageValidityType type;
    private Instant start;
    private Instant end;
    private Integer days;
    private Integer hours;

    protected CouponUsageValidity() {
    }

    public static CouponUsageValidity create(
        final CouponUsageValidityType type,
        final Instant start,
        final Instant end,
        final Integer days,
        final Integer hours
    ) {
        final CouponUsageValidity document = new CouponUsageValidity();
        document.type = type;
        document.start = start;
        document.end = end;
        document.days = days;
        document.hours = hours;
        return document;
    }

    public CouponUsageValidityType getType() {
        return type;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public Integer getDays() {
        return days;
    }

    public Integer getHours() {
        return hours;
    }
}

package im.dangmoo.benefit.domain.coupon.policy.usage;

import java.time.LocalTime;

public class CouponUsableTime {

    private LocalTime start;
    private LocalTime end;

    protected CouponUsableTime() {
    }

    public static CouponUsableTime create(final LocalTime start, final LocalTime end) {
        final CouponUsableTime document = new CouponUsableTime();
        document.start = start;
        document.end = end;
        return document;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }
}

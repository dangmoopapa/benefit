package im.dangmoo.benefit.domain.coupon.data.policy.issue;

import java.time.LocalTime;

public class CouponIssuableTime {

    private LocalTime start;
    private LocalTime end;

    private CouponIssuableTime() {
    }

    public static CouponIssuableTime create(final LocalTime start, final LocalTime end) {
        final CouponIssuableTime document = new CouponIssuableTime();
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

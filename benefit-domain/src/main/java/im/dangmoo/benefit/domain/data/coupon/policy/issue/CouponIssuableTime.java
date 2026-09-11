package im.dangmoo.benefit.domain.data.coupon.policy.issue;

import java.time.LocalTime;

public class CouponIssuableTime {

    private LocalTime start;
    private LocalTime end;

    private CouponIssuableTime() {
    }

    public static CouponIssuableTime create(final LocalTime start, final LocalTime end) {
        final CouponIssuableTime entity = new CouponIssuableTime();
        entity.start = start;
        entity.end = end;
        return entity;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }
}

package im.dangmoo.benefit.domain.data.coupon.policy.usage;

import java.time.LocalTime;

public class CouponUsableTime {

    private LocalTime start;
    private LocalTime end;

    private CouponUsableTime() {
    }

    public static CouponUsableTime create(final LocalTime start, final LocalTime end) {
        final CouponUsableTime entity = new CouponUsableTime();
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

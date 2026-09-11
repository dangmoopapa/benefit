package im.dangmoo.benefit.domain.data.coupon.policy.issue;

import java.time.Instant;

public class CouponIssuablePeriod {

    private Instant start;
    private Instant end;

    private CouponIssuablePeriod() {
    }

    public static CouponIssuablePeriod create(final Instant start, final Instant end) {
        final CouponIssuablePeriod entity = new CouponIssuablePeriod();
        entity.start = start;
        entity.end = end;
        return entity;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }
}

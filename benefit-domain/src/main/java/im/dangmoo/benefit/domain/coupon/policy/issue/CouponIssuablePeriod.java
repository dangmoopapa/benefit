package im.dangmoo.benefit.domain.coupon.policy.issue;

import java.time.Instant;

public class CouponIssuablePeriod {

    private Instant start;
    private Instant end;

    protected CouponIssuablePeriod() {
    }

    public static CouponIssuablePeriod create(final Instant start, final Instant end) {
        final CouponIssuablePeriod document = new CouponIssuablePeriod();
        document.start = start;
        document.end = end;
        return document;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }
}

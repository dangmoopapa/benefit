package im.dangmoo.benefit.admin.web.coupon.model.issue;

import im.dangmoo.benefit.domain.coupon.policy.issue.CouponIssuablePeriod;

import java.time.Instant;

public record CouponIssuablePeriodForm(
    Instant start,
    Instant end
) {

    public CouponIssuablePeriod toDocument() {
        return CouponIssuablePeriod.create(start, end);
    }

    public static CouponIssuablePeriodForm of(final CouponIssuablePeriod document) {
        return new CouponIssuablePeriodForm(document.getStart(), document.getEnd());
    }
}

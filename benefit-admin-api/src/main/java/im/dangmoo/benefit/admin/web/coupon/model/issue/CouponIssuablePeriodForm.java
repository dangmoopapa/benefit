package im.dangmoo.benefit.admin.web.coupon.model.issue;

import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssuablePeriod;

import java.time.Instant;

public record CouponIssuablePeriodForm(
    Instant start,
    Instant end
) {

    public CouponIssuablePeriod toEntity() {
        return CouponIssuablePeriod.create(start, end);
    }

    public static CouponIssuablePeriodForm of(final CouponIssuablePeriod entity) {
        return new CouponIssuablePeriodForm(entity.getStart(), entity.getEnd());
    }
}

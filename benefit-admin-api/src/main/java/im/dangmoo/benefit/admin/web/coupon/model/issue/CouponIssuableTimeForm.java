package im.dangmoo.benefit.admin.web.coupon.model.issue;

import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssuableTime;

import java.time.LocalTime;

public record CouponIssuableTimeForm(
    LocalTime start,
    LocalTime end
) {

    public CouponIssuableTime toEntity() {
        return CouponIssuableTime.create(start, end);
    }

    public static CouponIssuableTimeForm of(final CouponIssuableTime entity) {
        return new CouponIssuableTimeForm(entity.getStart(), entity.getEnd());
    }
}

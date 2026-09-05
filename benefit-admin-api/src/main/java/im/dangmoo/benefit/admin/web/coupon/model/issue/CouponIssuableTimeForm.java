package im.dangmoo.benefit.admin.web.coupon.model.issue;

import im.dangmoo.benefit.domain.coupon.document.policy.issue.CouponIssuableTime;

import java.time.LocalTime;

public record CouponIssuableTimeForm(
    LocalTime start,
    LocalTime end
) {

    public CouponIssuableTime toDocument() {
        return CouponIssuableTime.create(start, end);
    }

    public static CouponIssuableTimeForm of(final CouponIssuableTime document) {
        return new CouponIssuableTimeForm(document.getStart(), document.getEnd());
    }
}

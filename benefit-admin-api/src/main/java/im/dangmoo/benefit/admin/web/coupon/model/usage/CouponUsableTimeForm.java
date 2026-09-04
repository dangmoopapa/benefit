package im.dangmoo.benefit.admin.web.coupon.model.usage;

import im.dangmoo.benefit.domain.coupon.policy.usage.CouponUsableTime;

import java.time.LocalTime;

public record CouponUsableTimeForm(
    LocalTime start,
    LocalTime end
) {

    public CouponUsableTime toDocument() {
        return CouponUsableTime.create(start, end);
    }

    public static CouponUsableTimeForm of(final CouponUsableTime document) {
        return new CouponUsableTimeForm(document.getStart(), document.getEnd());
    }
}

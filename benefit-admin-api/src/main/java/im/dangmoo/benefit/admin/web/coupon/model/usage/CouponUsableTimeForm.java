package im.dangmoo.benefit.admin.web.coupon.model.usage;

import im.dangmoo.benefit.domain.data.coupon.policy.usage.CouponUsableTime;

import java.time.LocalTime;

public record CouponUsableTimeForm(
    LocalTime start,
    LocalTime end
) {

    public CouponUsableTime toEntity() {
        return CouponUsableTime.create(start, end);
    }

    public static CouponUsableTimeForm of(final CouponUsableTime entity) {
        return new CouponUsableTimeForm(entity.getStart(), entity.getEnd());
    }
}

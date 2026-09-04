package im.dangmoo.benefit.admin.web.coupon.model.usage;

import im.dangmoo.benefit.domain.coupon.policy.usage.CouponUsageValidity;
import im.dangmoo.benefit.domain.coupon.policy.usage.CouponUsageValidityType;

import java.time.Instant;

public record CouponUsageValidityForm(
    CouponUsageValidityType type,
    Instant start,
    Instant end,
    Integer days,
    Integer hours
) {

    public CouponUsageValidity toDocument() {
        return CouponUsageValidity.create(type, start, end, days, hours);
    }

    public static CouponUsageValidityForm of(final CouponUsageValidity document) {
        return new CouponUsageValidityForm(
            document.getType(),
            document.getStart(),
            document.getEnd(),
            document.getDays(),
            document.getHours()
        );
    }
}

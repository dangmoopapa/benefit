package im.dangmoo.benefit.admin.web.coupon.model.usage;

import im.dangmoo.benefit.domain.data.coupon.policy.usage.CouponUsageExpiration;
import im.dangmoo.benefit.domain.data.coupon.policy.usage.CouponUsageExpirationType;

import java.time.Instant;

public record CouponUsageExpirationForm(
    CouponUsageExpirationType type,
    Instant start,
    Instant end,
    Integer days,
    Integer hours
) {

    public CouponUsageExpiration toDocument() {
        return CouponUsageExpiration.create(type, start, end, days, hours);
    }

    public static CouponUsageExpirationForm of(final CouponUsageExpiration document) {
        return new CouponUsageExpirationForm(
            document.getType(),
            document.getStart(),
            document.getEnd(),
            document.getDays(),
            document.getHours()
        );
    }
}

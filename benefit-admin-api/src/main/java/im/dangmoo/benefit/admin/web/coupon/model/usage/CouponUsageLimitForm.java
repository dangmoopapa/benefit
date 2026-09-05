package im.dangmoo.benefit.admin.web.coupon.model.usage;

import im.dangmoo.benefit.domain.coupon.data.policy.usage.CouponUsageLimit;

public record CouponUsageLimitForm(
    Integer perUser,
    Integer perUserPerMonth,
    Integer perOrder,
    Long total,
    Integer perDay,
    Integer perHour
) {

    public CouponUsageLimit toDocument() {
        return CouponUsageLimit.create(perUser, perUserPerMonth, perOrder, total, perDay, perHour);
    }

    public static CouponUsageLimitForm of(final CouponUsageLimit document) {
        return new CouponUsageLimitForm(
            document.getPerUser(),
            document.getPerUserPerMonth(),
            document.getPerOrder(),
            document.getTotal(),
            document.getPerDay(),
            document.getPerHour()
        );
    }
}

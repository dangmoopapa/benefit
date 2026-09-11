package im.dangmoo.benefit.admin.web.coupon.model.usage;

import im.dangmoo.benefit.domain.data.coupon.policy.usage.CouponUsageLimit;

public record CouponUsageLimitForm(
    Integer perUser,
    Integer perUserPerMonth,
    Integer perOrder,
    Long total,
    Integer perDay,
    Integer perHour
) {

    public CouponUsageLimit toEntity() {
        return CouponUsageLimit.create(perUser, perUserPerMonth, perOrder, total, perDay, perHour);
    }

    public static CouponUsageLimitForm of(final CouponUsageLimit entity) {
        return new CouponUsageLimitForm(
            entity.getPerUser(),
            entity.getPerUserPerMonth(),
            entity.getPerOrder(),
            entity.getTotal(),
            entity.getPerDay(),
            entity.getPerHour()
        );
    }
}

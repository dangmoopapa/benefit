package im.dangmoo.benefit.domain.coupon.document.policy.usage;

public record CouponUsageCountSnapshot(
    long perUser,
    long perUserPerMonth,
    long perOrder,
    long total,
    long perDay,
    long perHour
) {
}

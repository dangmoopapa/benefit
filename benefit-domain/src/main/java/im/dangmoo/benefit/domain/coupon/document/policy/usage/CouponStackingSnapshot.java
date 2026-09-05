package im.dangmoo.benefit.domain.coupon.document.policy.usage;

public record CouponStackingSnapshot(
    boolean otherCoupon,
    boolean productAndOrderTogether,
    boolean point,
    boolean promotion,
    boolean freeShipping
) {
}

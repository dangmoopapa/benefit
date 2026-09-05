package im.dangmoo.benefit.admin.web.coupon.model.usage;

import im.dangmoo.benefit.domain.coupon.document.policy.usage.CouponStackingCondition;

public record CouponStackingForm(
    boolean combinableWithOtherCoupons,
    boolean combinableWithProductAndOrder,
    boolean combinableWithPoint,
    boolean combinableWithPromotion,
    boolean combinableWithFreeShipping,
    int priority,
    boolean autoSelectMaxDiscount
) {

    public CouponStackingCondition toDocument() {
        return CouponStackingCondition.create(
            combinableWithOtherCoupons,
            combinableWithProductAndOrder,
            combinableWithPoint,
            combinableWithPromotion,
            combinableWithFreeShipping,
            priority,
            autoSelectMaxDiscount
        );
    }

    public static CouponStackingForm of(final CouponStackingCondition document) {
        return new CouponStackingForm(
            document.isCombinableWithOtherCoupons(),
            document.isCombinableWithProductAndOrder(),
            document.isCombinableWithPoint(),
            document.isCombinableWithPromotion(),
            document.isCombinableWithFreeShipping(),
            document.getPriority(),
            document.isAutoSelectMaxDiscount()
        );
    }
}

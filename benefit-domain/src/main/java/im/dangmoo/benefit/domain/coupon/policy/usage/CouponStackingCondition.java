package im.dangmoo.benefit.domain.coupon.policy.usage;

public class CouponStackingCondition {

    private boolean combinableWithOtherCoupons;
    private boolean combinableWithProductAndOrder;
    private boolean combinableWithPoint;
    private boolean combinableWithPromotion;
    private boolean combinableWithFreeShipping;
    private int priority;
    private boolean autoSelectMaxDiscount;

    private CouponStackingCondition() {
    }

    public static CouponStackingCondition create(
        final boolean combinableWithOtherCoupons,
        final boolean combinableWithProductAndOrder,
        final boolean combinableWithPoint,
        final boolean combinableWithPromotion,
        final boolean combinableWithFreeShipping,
        final int priority,
        final boolean autoSelectMaxDiscount
    ) {
        final CouponStackingCondition document = new CouponStackingCondition();
        document.combinableWithOtherCoupons = combinableWithOtherCoupons;
        document.combinableWithProductAndOrder = combinableWithProductAndOrder;
        document.combinableWithPoint = combinableWithPoint;
        document.combinableWithPromotion = combinableWithPromotion;
        document.combinableWithFreeShipping = combinableWithFreeShipping;
        document.priority = priority;
        document.autoSelectMaxDiscount = autoSelectMaxDiscount;
        return document;
    }

    public boolean isCombinableWithOtherCoupons() {
        return combinableWithOtherCoupons;
    }

    public boolean isCombinableWithProductAndOrder() {
        return combinableWithProductAndOrder;
    }

    public boolean isCombinableWithPoint() {
        return combinableWithPoint;
    }

    public boolean isCombinableWithPromotion() {
        return combinableWithPromotion;
    }

    public boolean isCombinableWithFreeShipping() {
        return combinableWithFreeShipping;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isAutoSelectMaxDiscount() {
        return autoSelectMaxDiscount;
    }

    public boolean isSatisfiedBy(final CouponStackingSnapshot stacking) {
        if (stacking.otherCoupon() && !combinableWithOtherCoupons) {
            return false;
        }
        if (stacking.productAndOrderTogether() && !combinableWithProductAndOrder) {
            return false;
        }
        if (stacking.point() && !combinableWithPoint) {
            return false;
        }
        if (stacking.promotion() && !combinableWithPromotion) {
            return false;
        }
        return !stacking.freeShipping() || combinableWithFreeShipping;
    }
}

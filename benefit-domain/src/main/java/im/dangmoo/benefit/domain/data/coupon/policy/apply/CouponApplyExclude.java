package im.dangmoo.benefit.domain.data.coupon.policy.apply;

import java.util.List;

public class CouponApplyExclude {

    private List<String> productIds;
    private List<String> categoryIds;
    private boolean alreadyDiscounted;
    private boolean otherCouponApplied;

    private CouponApplyExclude() {
    }

    public static CouponApplyExclude create(
        final List<String> productIds,
        final List<String> categoryIds,
        final boolean alreadyDiscounted,
        final boolean otherCouponApplied
    ) {
        final CouponApplyExclude entity = new CouponApplyExclude();
        entity.productIds = productIds;
        entity.categoryIds = categoryIds;
        entity.alreadyDiscounted = alreadyDiscounted;
        entity.otherCouponApplied = otherCouponApplied;
        return entity;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public boolean isAlreadyDiscounted() {
        return alreadyDiscounted;
    }

    public boolean isOtherCouponApplied() {
        return otherCouponApplied;
    }
}

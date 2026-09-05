package im.dangmoo.benefit.domain.coupon.data.policy.apply;

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
        final CouponApplyExclude document = new CouponApplyExclude();
        document.productIds = productIds;
        document.categoryIds = categoryIds;
        document.alreadyDiscounted = alreadyDiscounted;
        document.otherCouponApplied = otherCouponApplied;
        return document;
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

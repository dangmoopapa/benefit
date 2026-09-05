package im.dangmoo.benefit.admin.web.coupon.model.apply;

import im.dangmoo.benefit.domain.coupon.data.policy.apply.CouponApplyExclude;

import java.util.List;

public record CouponApplyExcludeForm(
    List<String> productIds,
    List<String> categoryIds,
    boolean alreadyDiscounted,
    boolean otherCouponApplied
) {

    public CouponApplyExclude toDocument() {
        return CouponApplyExclude.create(productIds, categoryIds, alreadyDiscounted, otherCouponApplied);
    }

    public static CouponApplyExcludeForm of(final CouponApplyExclude document) {
        return new CouponApplyExcludeForm(
            document.getProductIds(),
            document.getCategoryIds(),
            document.isAlreadyDiscounted(),
            document.isOtherCouponApplied()
        );
    }
}

package im.dangmoo.benefit.admin.web.coupon.model.apply;

import im.dangmoo.benefit.domain.data.coupon.policy.apply.CouponApplyExclude;

import java.util.List;

public record CouponApplyExcludeForm(
    List<String> productIds,
    List<String> categoryIds,
    boolean alreadyDiscounted,
    boolean otherCouponApplied
) {

    public CouponApplyExclude toEntity() {
        return CouponApplyExclude.create(productIds, categoryIds, alreadyDiscounted, otherCouponApplied);
    }

    public static CouponApplyExcludeForm of(final CouponApplyExclude entity) {
        return new CouponApplyExcludeForm(
            entity.getProductIds(),
            entity.getCategoryIds(),
            entity.isAlreadyDiscounted(),
            entity.isOtherCouponApplied()
        );
    }
}

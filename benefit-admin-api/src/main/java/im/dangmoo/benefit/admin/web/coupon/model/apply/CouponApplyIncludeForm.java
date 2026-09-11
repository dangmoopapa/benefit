package im.dangmoo.benefit.admin.web.coupon.model.apply;

import im.dangmoo.benefit.domain.data.coupon.policy.apply.CouponApplyInclude;

import java.util.List;

public record CouponApplyIncludeForm(
    boolean allProducts,
    List<String> productIds,
    List<String> categoryIds,
    List<String> brandIds,
    List<String> sellerIds,
    List<String> optionIds
) {

    public CouponApplyInclude toEntity() {
        return CouponApplyInclude.create(allProducts, productIds, categoryIds, brandIds, sellerIds, optionIds);
    }

    public static CouponApplyIncludeForm of(final CouponApplyInclude entity) {
        return new CouponApplyIncludeForm(
            entity.isAllProducts(),
            entity.getProductIds(),
            entity.getCategoryIds(),
            entity.getBrandIds(),
            entity.getSellerIds(),
            entity.getOptionIds()
        );
    }
}

package im.dangmoo.benefit.admin.web.coupon.model.apply;

import im.dangmoo.benefit.domain.coupon.policy.apply.CouponApplyInclude;

import java.util.List;

public record CouponApplyIncludeForm(
    boolean allProducts,
    List<String> productIds,
    List<String> categoryIds,
    List<String> brandIds,
    List<String> sellerIds,
    List<String> optionIds
) {

    public CouponApplyInclude toDocument() {
        return CouponApplyInclude.create(allProducts, productIds, categoryIds, brandIds, sellerIds, optionIds);
    }

    public static CouponApplyIncludeForm of(final CouponApplyInclude document) {
        return new CouponApplyIncludeForm(
            document.isAllProducts(),
            document.getProductIds(),
            document.getCategoryIds(),
            document.getBrandIds(),
            document.getSellerIds(),
            document.getOptionIds()
        );
    }
}

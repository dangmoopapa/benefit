package im.dangmoo.benefit.domain.coupon.data.policy.apply;

import java.util.List;

public class CouponApplyInclude {

    private boolean allProducts;
    private List<String> productIds;
    private List<String> categoryIds;
    private List<String> brandIds;
    private List<String> sellerIds;
    private List<String> optionIds;

    private CouponApplyInclude() {
    }

    public static CouponApplyInclude create(
        final boolean allProducts,
        final List<String> productIds,
        final List<String> categoryIds,
        final List<String> brandIds,
        final List<String> sellerIds,
        final List<String> optionIds
    ) {
        final CouponApplyInclude document = new CouponApplyInclude();
        document.allProducts = allProducts;
        document.productIds = productIds;
        document.categoryIds = categoryIds;
        document.brandIds = brandIds;
        document.sellerIds = sellerIds;
        document.optionIds = optionIds;
        return document;
    }

    public boolean isAllProducts() {
        return allProducts;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public List<String> getBrandIds() {
        return brandIds;
    }

    public List<String> getSellerIds() {
        return sellerIds;
    }

    public List<String> getOptionIds() {
        return optionIds;
    }
}

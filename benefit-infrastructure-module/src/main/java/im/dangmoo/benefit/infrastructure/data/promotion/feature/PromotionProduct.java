package im.dangmoo.benefit.infrastructure.data.promotion.feature;

public class PromotionProduct {

    private String productId;
    private String name;
    private String imageUrl;
    private Integer sortOrder;

    private PromotionProduct() {
    }

    public static PromotionProduct create(
        final String productId,
        final String name,
        final String imageUrl,
        final Integer sortOrder
    ) {
        final PromotionProduct product = new PromotionProduct();
        product.productId = productId;
        product.name = name;
        product.imageUrl = imageUrl;
        product.sortOrder = sortOrder;
        return product;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }
}

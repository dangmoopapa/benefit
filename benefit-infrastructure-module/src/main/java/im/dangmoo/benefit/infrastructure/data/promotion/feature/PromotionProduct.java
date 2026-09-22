package im.dangmoo.benefit.infrastructure.data.promotion.feature;

public class PromotionProduct {

    private String productId;
    private String name;
    private String imageUrl;
    private Integer sortOrder;

    private PromotionProduct() {
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

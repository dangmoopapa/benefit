package im.dangmoo.benefit.data.entity.promotion.banner;

public class PromotionBannerItem {

    private String policyKey;
    private String bannerImageUrl;
    private Integer sortOrder;

    private PromotionBannerItem() {
    }

    public static PromotionBannerItem of(
        final String policyKey,
        final String bannerImageUrl,
        final Integer sortOrder
    ) {
        final PromotionBannerItem item = new PromotionBannerItem();
        item.policyKey = policyKey;
        item.bannerImageUrl = bannerImageUrl;
        item.sortOrder = sortOrder == null ? 0 : sortOrder;
        return item;
    }

    public String getPolicyKey() {
        return policyKey;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }
}

package im.dangmoo.benefit.api.model.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerItem;

import java.util.List;

public record PromotionBannerResponse(
    String key,
    List<Item> items
) {

    public static PromotionBannerResponse of(final String key, final List<Item> items) {
        return new PromotionBannerResponse(key, items);
    }

    public record Item(
        String policyKey,
        String bannerImageUrl,
        Integer sortOrder
    ) {

        public static Item of(final PromotionBannerItem item) {
            return new Item(
                item.getPolicyKey(),
                item.getBannerImageUrl(),
                item.getSortOrder()
            );
        }
    }
}

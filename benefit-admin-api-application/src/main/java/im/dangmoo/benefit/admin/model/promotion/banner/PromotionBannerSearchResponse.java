package im.dangmoo.benefit.admin.model.promotion.banner;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerDocument;
import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerItem;
import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerStatus;

import java.util.List;

public record PromotionBannerSearchResponse(List<Item> items) {

    public static PromotionBannerSearchResponse of(final List<PromotionBannerDocument> banners) {
        return new PromotionBannerSearchResponse(
            banners.stream().map(Item::of).toList()
        );
    }

    public record Item(
        String id,
        String key,
        String name,
        PromotionBannerStatus status,
        List<PromotionBannerItem> items
    ) {
        public static Item of(final PromotionBannerDocument banner) {
            return new Item(
                banner.getId(),
                banner.getKey(),
                banner.getName(),
                banner.getStatus(),
                banner.getItems()
            );
        }
    }
}

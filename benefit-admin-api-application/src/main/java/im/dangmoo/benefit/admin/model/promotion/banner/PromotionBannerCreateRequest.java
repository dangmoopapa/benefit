package im.dangmoo.benefit.admin.model.promotion.banner;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBanner;
import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerItem;

import java.util.List;

public record PromotionBannerCreateRequest(
    String key,
    String name,
    List<BannerItem> items
) {

    public PromotionBanner toDocument(final String createdBy) {
        return PromotionBanner.create(
            key,
            name,
            items == null ? List.of() : items.stream().map(BannerItem::toDocument).toList(),
            createdBy
        );
    }

    public record BannerItem(String policyKey, String bannerImageUrl, Integer sortOrder) {

        public PromotionBannerItem toDocument() {
            return PromotionBannerItem.of(policyKey, bannerImageUrl, sortOrder);
        }
    }
}

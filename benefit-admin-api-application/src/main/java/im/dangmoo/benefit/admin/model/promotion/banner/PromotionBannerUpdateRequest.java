package im.dangmoo.benefit.admin.model.promotion.banner;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBanner;
import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerItem;

import java.util.List;

public record PromotionBannerUpdateRequest(
    String name,
    List<BannerItem> items
) {

    public PromotionBanner toUpdate(final PromotionBanner banner, final String updatedBy) {
        return banner.update(
            name,
            items == null ? List.of() : items.stream().map(BannerItem::toDocument).toList(),
            updatedBy
        );
    }

    public record BannerItem(String policyKey, String bannerImageUrl, Integer sortOrder) {

        public PromotionBannerItem toDocument() {
            return PromotionBannerItem.of(policyKey, bannerImageUrl, sortOrder);
        }
    }
}

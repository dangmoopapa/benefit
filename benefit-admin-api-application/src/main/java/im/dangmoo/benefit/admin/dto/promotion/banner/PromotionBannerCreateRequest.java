package im.dangmoo.benefit.admin.dto.promotion.banner;

import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerDocument;
import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerItem;

import java.util.List;

public record PromotionBannerCreateRequest(
    String key,
    String name,
    List<BannerItem> items
) {

    public PromotionBannerDocument toDocument(final String createdBy) {
        return PromotionBannerDocument.create(
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

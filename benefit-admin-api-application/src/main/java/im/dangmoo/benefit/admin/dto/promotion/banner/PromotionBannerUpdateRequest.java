package im.dangmoo.benefit.admin.dto.promotion.banner;

import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerDocument;
import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerItem;

import java.util.List;

public record PromotionBannerUpdateRequest(
    String name,
    List<BannerItem> items
) {

    public PromotionBannerDocument toUpdate(final PromotionBannerDocument banner, final String updatedBy) {
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

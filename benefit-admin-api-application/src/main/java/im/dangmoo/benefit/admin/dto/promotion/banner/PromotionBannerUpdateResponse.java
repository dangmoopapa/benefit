package im.dangmoo.benefit.admin.dto.promotion.banner;

import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerDocument;

public record PromotionBannerUpdateResponse(String id, String key) {

    public static PromotionBannerUpdateResponse of(final PromotionBannerDocument banner) {
        return new PromotionBannerUpdateResponse(banner.getId(), banner.getKey());
    }
}

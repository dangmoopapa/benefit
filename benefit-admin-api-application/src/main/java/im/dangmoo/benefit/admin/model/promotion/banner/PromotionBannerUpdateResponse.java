package im.dangmoo.benefit.admin.model.promotion.banner;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerDocument;

public record PromotionBannerUpdateResponse(String id, String key) {

    public static PromotionBannerUpdateResponse of(final PromotionBannerDocument banner) {
        return new PromotionBannerUpdateResponse(banner.getId(), banner.getKey());
    }
}

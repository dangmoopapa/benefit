package im.dangmoo.benefit.admin.model.promotion.banner;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBanner;

public record PromotionBannerUpdateResponse(String id, String key) {

    public static PromotionBannerUpdateResponse of(final PromotionBanner banner) {
        return new PromotionBannerUpdateResponse(banner.getId(), banner.getKey());
    }
}

package im.dangmoo.benefit.admin.model.promotion.banner;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBanner;

public record PromotionBannerCreateResponse(String id, String key) {

    public static PromotionBannerCreateResponse of(final PromotionBanner banner) {
        return new PromotionBannerCreateResponse(banner.getId(), banner.getKey());
    }
}

package im.dangmoo.benefit.admin.model.promotion.banner;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerDocument;

public record PromotionBannerCreateResponse(String id, String key) {

    public static PromotionBannerCreateResponse of(final PromotionBannerDocument banner) {
        return new PromotionBannerCreateResponse(banner.getId(), banner.getKey());
    }
}

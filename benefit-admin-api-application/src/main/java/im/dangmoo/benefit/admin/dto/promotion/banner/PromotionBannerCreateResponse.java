package im.dangmoo.benefit.admin.dto.promotion.banner;

import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerDocument;

public record PromotionBannerCreateResponse(String id, String key) {

    public static PromotionBannerCreateResponse of(final PromotionBannerDocument banner) {
        return new PromotionBannerCreateResponse(banner.getId(), banner.getKey());
    }
}

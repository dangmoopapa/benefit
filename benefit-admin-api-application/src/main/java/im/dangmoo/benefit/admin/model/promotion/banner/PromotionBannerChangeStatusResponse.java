package im.dangmoo.benefit.admin.model.promotion.banner;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBanner;
import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerStatus;

public record PromotionBannerChangeStatusResponse(String id, PromotionBannerStatus status) {

    public static PromotionBannerChangeStatusResponse of(final PromotionBanner banner) {
        return new PromotionBannerChangeStatusResponse(banner.getId(), banner.getStatus());
    }
}

package im.dangmoo.benefit.admin.model.promotion.banner;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerDocument;
import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerStatus;

public record PromotionBannerChangeStatusResponse(String id, PromotionBannerStatus status) {

    public static PromotionBannerChangeStatusResponse of(final PromotionBannerDocument banner) {
        return new PromotionBannerChangeStatusResponse(banner.getId(), banner.getStatus());
    }
}

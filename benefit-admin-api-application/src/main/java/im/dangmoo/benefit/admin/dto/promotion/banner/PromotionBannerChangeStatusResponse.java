package im.dangmoo.benefit.admin.dto.promotion.banner;

import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerDocument;
import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerStatus;

public record PromotionBannerChangeStatusResponse(String id, PromotionBannerStatus status) {

    public static PromotionBannerChangeStatusResponse of(final PromotionBannerDocument banner) {
        return new PromotionBannerChangeStatusResponse(banner.getId(), banner.getStatus());
    }
}

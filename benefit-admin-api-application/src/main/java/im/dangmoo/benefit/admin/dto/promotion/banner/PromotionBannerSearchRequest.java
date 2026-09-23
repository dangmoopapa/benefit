package im.dangmoo.benefit.admin.dto.promotion.banner;

import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerStatus;

public record PromotionBannerSearchRequest(
    String key,
    String name,
    PromotionBannerStatus status
) {
}

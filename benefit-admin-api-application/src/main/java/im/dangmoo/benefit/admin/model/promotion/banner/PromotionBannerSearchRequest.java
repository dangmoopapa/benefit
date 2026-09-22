package im.dangmoo.benefit.admin.model.promotion.banner;

import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerStatus;

public record PromotionBannerSearchRequest(
    String key,
    String name,
    PromotionBannerStatus status
) {
}

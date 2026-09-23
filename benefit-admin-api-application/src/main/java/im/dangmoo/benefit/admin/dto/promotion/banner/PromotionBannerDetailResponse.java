package im.dangmoo.benefit.admin.dto.promotion.banner;

import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerDocument;
import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerItem;
import im.dangmoo.benefit.data.entity.promotion.banner.PromotionBannerStatus;

import java.time.Instant;
import java.util.List;

public record PromotionBannerDetailResponse(
    String id,
    String key,
    String name,
    PromotionBannerStatus status,
    List<PromotionBannerItem> items,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static PromotionBannerDetailResponse of(final PromotionBannerDocument banner) {
        return new PromotionBannerDetailResponse(
            banner.getId(),
            banner.getKey(),
            banner.getName(),
            banner.getStatus(),
            banner.getItems(),
            banner.getCreatedBy(),
            banner.getCreatedAt(),
            banner.getUpdatedBy(),
            banner.getUpdatedAt()
        );
    }
}

package im.dangmoo.benefit.admin.model.promotion.policy;

import im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;

import java.time.Instant;
import java.util.List;

public record PromotionPolicyDetailResponse(
    String id,
    String key,
    String thumbnailImageUrl,
    String title,
    String description,
    Instant startAt,
    Instant endAt,
    PromotionPolicyStatus status,
    String content,
    String disclaimer,
    List<PromotionFeature> features,
    Integer sortOrder,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static PromotionPolicyDetailResponse of(final PromotionPolicy policy) {
        return new PromotionPolicyDetailResponse(
            policy.getId(),
            policy.getKey(),
            policy.getThumbnailImageUrl(),
            policy.getTitle(),
            policy.getDescription(),
            policy.getStartAt(),
            policy.getEndAt(),
            policy.getStatus(),
            policy.getContent(),
            policy.getDisclaimer(),
            policy.getFeatures(),
            policy.getSortOrder(),
            policy.getCreatedBy(),
            policy.getCreatedAt(),
            policy.getUpdatedBy(),
            policy.getUpdatedAt()
        );
    }
}

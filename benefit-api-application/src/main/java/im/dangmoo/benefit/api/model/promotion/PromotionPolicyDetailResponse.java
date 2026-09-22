package im.dangmoo.benefit.api.model.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;

import java.time.Instant;
import java.util.List;

public record PromotionPolicyDetailResponse(
    String key,
    String thumbnailImageUrl,
    String title,
    String description,
    Instant startAt,
    Instant endAt,
    String content,
    String disclaimer,
    List<PromotionFeature> features,
    Integer sortOrder,
    boolean applied
) {

    public static PromotionPolicyDetailResponse of(final PromotionPolicy policy, final boolean applied) {
        return new PromotionPolicyDetailResponse(
            policy.getKey(),
            policy.getThumbnailImageUrl(),
            policy.getTitle(),
            policy.getDescription(),
            policy.getStartAt(),
            policy.getEndAt(),
            policy.getContent(),
            policy.getDisclaimer(),
            policy.getFeatures(),
            policy.getSortOrder(),
            applied
        );
    }
}

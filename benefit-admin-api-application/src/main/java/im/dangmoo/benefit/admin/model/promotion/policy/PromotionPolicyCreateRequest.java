package im.dangmoo.benefit.admin.model.promotion.policy;

import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;

import java.time.Instant;
import java.util.List;

public record PromotionPolicyCreateRequest(
    String key,
    String thumbnailImageUrl,
    String title,
    String description,
    Instant startAt,
    Instant endAt,
    String content,
    String disclaimer,
    List<PromotionFeature> features,
    Integer sortOrder
) {

    public PromotionPolicy toDocument(final String createdBy) {
        return PromotionPolicy.create(
            key,
            thumbnailImageUrl,
            title,
            description,
            startAt,
            endAt,
            content,
            disclaimer,
            features,
            sortOrder,
            createdBy
        );
    }
}

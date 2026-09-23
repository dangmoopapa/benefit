package im.dangmoo.benefit.admin.dto.promotion.policy;

import im.dangmoo.benefit.data.entity.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.data.entity.promotion.policy.PromotionPolicyDocument;

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

    public PromotionPolicyDocument toDocument(final String createdBy) {
        return PromotionPolicyDocument.create(
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

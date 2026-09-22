package im.dangmoo.benefit.admin.model.promotion.policy;

import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;

import java.time.Instant;
import java.util.List;

public record PromotionPolicyUpdateRequest(
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

    public PromotionPolicy toUpdate(final PromotionPolicy policy, final String updatedBy) {
        return policy.update(
            thumbnailImageUrl,
            title,
            description,
            startAt,
            endAt,
            content,
            disclaimer,
            features,
            sortOrder,
            updatedBy
        );
    }
}

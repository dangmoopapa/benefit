package im.dangmoo.benefit.admin.dto.promotion.policy;

import im.dangmoo.benefit.data.entity.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.data.entity.promotion.policy.PromotionPolicyDocument;

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

    public PromotionPolicyDocument toUpdate(final PromotionPolicyDocument policy, final String updatedBy) {
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

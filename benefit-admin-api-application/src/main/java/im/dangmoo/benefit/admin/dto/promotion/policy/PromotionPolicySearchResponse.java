package im.dangmoo.benefit.admin.dto.promotion.policy;

import im.dangmoo.benefit.data.entity.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.data.entity.promotion.policy.PromotionPolicyDocument;

import java.time.Instant;
import java.util.List;

public record PromotionPolicySearchResponse(List<Item> items) {

    public static PromotionPolicySearchResponse of(final List<PromotionPolicyDocument> policies) {
        return new PromotionPolicySearchResponse(
            policies.stream().map(Item::of).toList()
        );
    }

    public record Item(
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
        Integer sortOrder
    ) {
        public static Item of(final PromotionPolicyDocument policy) {
            return new Item(
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
                policy.getSortOrder()
            );
        }
    }
}

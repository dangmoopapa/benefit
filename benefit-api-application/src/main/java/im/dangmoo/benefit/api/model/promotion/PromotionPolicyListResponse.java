package im.dangmoo.benefit.api.model.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;

import java.time.Instant;
import java.util.List;

public record PromotionPolicyListResponse(
    List<Item> items
) {

    public static PromotionPolicyListResponse of(final List<PromotionPolicy> policies) {
        return new PromotionPolicyListResponse(
            policies.stream().map(Item::of).toList()
        );
    }

    public record Item(
        String key,
        String thumbnailImageUrl,
        String title,
        String description,
        Instant startAt,
        Instant endAt,
        Integer sortOrder
    ) {

        public static Item of(final PromotionPolicy policy) {
            return new Item(
                policy.getKey(),
                policy.getThumbnailImageUrl(),
                policy.getTitle(),
                policy.getDescription(),
                policy.getStartAt(),
                policy.getEndAt(),
                policy.getSortOrder()
            );
        }
    }
}

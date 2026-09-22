package im.dangmoo.benefit.admin.model.promotion.applier;

import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplier;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplierStatus;

import java.time.Instant;
import java.util.List;

public record PromotionApplierSearchResponse(List<Item> items) {

    public static PromotionApplierSearchResponse of(final List<PromotionApplier> appliers) {
        return new PromotionApplierSearchResponse(
            appliers.stream().map(Item::of).toList()
        );
    }

    public record Item(
        String id,
        String userId,
        Instant appliedAt,
        PromotionApplierStatus status
    ) {
        public static Item of(final PromotionApplier applier) {
            return new Item(
                applier.getId(),
                applier.getUserId(),
                applier.getAppliedAt(),
                applier.getStatus()
            );
        }
    }
}

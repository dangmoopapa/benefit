package im.dangmoo.benefit.admin.dto.promotion.applier;

import im.dangmoo.benefit.data.entity.promotion.applier.PromotionApplierDocument;
import im.dangmoo.benefit.data.entity.promotion.applier.PromotionApplierStatus;

import java.time.Instant;
import java.util.List;

public record PromotionApplierSearchResponse(List<Item> items) {

    public static PromotionApplierSearchResponse of(final List<PromotionApplierDocument> appliers) {
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
        public static Item of(final PromotionApplierDocument applier) {
            return new Item(
                applier.getId(),
                applier.getUserId(),
                applier.getAppliedAt(),
                applier.getStatus()
            );
        }
    }
}

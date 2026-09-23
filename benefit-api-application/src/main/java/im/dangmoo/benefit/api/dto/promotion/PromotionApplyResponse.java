package im.dangmoo.benefit.api.dto.promotion;

import im.dangmoo.benefit.data.entity.promotion.applier.PromotionApplierDocument;

import java.time.Instant;

public record PromotionApplyResponse(
    String policyKey,
    String userId,
    Instant appliedAt
) {

    public static PromotionApplyResponse of(final PromotionApplierDocument applier) {
        return new PromotionApplyResponse(
            applier.getPolicyKey(),
            applier.getUserId(),
            applier.getAppliedAt()
        );
    }
}

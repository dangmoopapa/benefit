package im.dangmoo.benefit.api.model.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplier;

import java.time.Instant;

public record PromotionApplyResponse(
    String policyKey,
    String userId,
    Instant appliedAt
) {

    public static PromotionApplyResponse of(final PromotionApplier applier) {
        return new PromotionApplyResponse(
            applier.getPolicyKey(),
            applier.getUserId(),
            applier.getAppliedAt()
        );
    }
}

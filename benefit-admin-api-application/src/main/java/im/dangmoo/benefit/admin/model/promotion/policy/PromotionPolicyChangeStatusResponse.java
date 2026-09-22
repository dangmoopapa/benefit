package im.dangmoo.benefit.admin.model.promotion.policy;

import im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;

public record PromotionPolicyChangeStatusResponse(String id, PromotionPolicyStatus status) {

    public static PromotionPolicyChangeStatusResponse of(final PromotionPolicy policy) {
        return new PromotionPolicyChangeStatusResponse(policy.getId(), policy.getStatus());
    }
}

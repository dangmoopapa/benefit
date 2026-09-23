package im.dangmoo.benefit.admin.model.promotion.policy;

import im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyDocument;

public record PromotionPolicyChangeStatusResponse(String id, PromotionPolicyStatus status) {

    public static PromotionPolicyChangeStatusResponse of(final PromotionPolicyDocument policy) {
        return new PromotionPolicyChangeStatusResponse(policy.getId(), policy.getStatus());
    }
}

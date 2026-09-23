package im.dangmoo.benefit.admin.dto.promotion.policy;

import im.dangmoo.benefit.data.entity.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.data.entity.promotion.policy.PromotionPolicyDocument;

public record PromotionPolicyChangeStatusResponse(String id, PromotionPolicyStatus status) {

    public static PromotionPolicyChangeStatusResponse of(final PromotionPolicyDocument policy) {
        return new PromotionPolicyChangeStatusResponse(policy.getId(), policy.getStatus());
    }
}

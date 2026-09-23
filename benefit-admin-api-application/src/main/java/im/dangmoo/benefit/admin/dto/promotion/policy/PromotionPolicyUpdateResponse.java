package im.dangmoo.benefit.admin.dto.promotion.policy;

import im.dangmoo.benefit.data.entity.promotion.policy.PromotionPolicyDocument;

public record PromotionPolicyUpdateResponse(String id, String key) {

    public static PromotionPolicyUpdateResponse of(final PromotionPolicyDocument policy) {
        return new PromotionPolicyUpdateResponse(policy.getId(), policy.getKey());
    }
}

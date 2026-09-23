package im.dangmoo.benefit.admin.model.promotion.policy;

import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyDocument;

public record PromotionPolicyCreateResponse(String id, String key) {

    public static PromotionPolicyCreateResponse of(final PromotionPolicyDocument policy) {
        return new PromotionPolicyCreateResponse(policy.getId(), policy.getKey());
    }
}

package im.dangmoo.benefit.admin.dto.promotion.policy;

import im.dangmoo.benefit.data.entity.promotion.policy.PromotionPolicyDocument;

public record PromotionPolicyCreateResponse(String id, String key) {

    public static PromotionPolicyCreateResponse of(final PromotionPolicyDocument policy) {
        return new PromotionPolicyCreateResponse(policy.getId(), policy.getKey());
    }
}

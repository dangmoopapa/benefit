package im.dangmoo.benefit.admin.model.promotion.policy;

import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;

public record PromotionPolicyCreateResponse(String id, String key) {

    public static PromotionPolicyCreateResponse of(final PromotionPolicy policy) {
        return new PromotionPolicyCreateResponse(policy.getId(), policy.getKey());
    }
}

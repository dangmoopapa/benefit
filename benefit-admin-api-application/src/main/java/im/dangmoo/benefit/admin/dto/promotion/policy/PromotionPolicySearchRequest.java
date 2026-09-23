package im.dangmoo.benefit.admin.dto.promotion.policy;

import im.dangmoo.benefit.data.entity.promotion.PromotionPolicyStatus;

public record PromotionPolicySearchRequest(
    String key,
    String title,
    PromotionPolicyStatus status
) {
}

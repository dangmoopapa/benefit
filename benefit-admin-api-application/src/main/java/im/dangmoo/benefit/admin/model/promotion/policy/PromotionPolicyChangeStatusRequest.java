package im.dangmoo.benefit.admin.model.promotion.policy;

import im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus;

public record PromotionPolicyChangeStatusRequest(PromotionPolicyStatus status) {
}

package im.dangmoo.benefit.admin.web.coupon.model;

import im.dangmoo.benefit.admin.web.coupon.model.apply.CouponApplyForm;
import im.dangmoo.benefit.admin.web.coupon.model.benefit.CouponBenefitForm;
import im.dangmoo.benefit.admin.web.coupon.model.issue.CouponIssueForm;
import im.dangmoo.benefit.admin.web.coupon.model.lifecycle.CouponLifecycleForm;
import im.dangmoo.benefit.admin.web.coupon.model.usage.CouponUsageForm;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyType;

import java.time.Instant;

public record CouponPolicyResponse(
    String id,
    String code,
    String name,
    String description,
    String platformId,
    CouponPolicyType type,
    CouponPolicyStatus status,
    CouponIssueForm issueCondition,
    CouponBenefitForm benefitCondition,
    CouponApplyForm applyCondition,
    CouponUsageForm usageCondition,
    CouponLifecycleForm lifecycleCondition,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static CouponPolicyResponse of(final CouponPolicy entity) {
        return new CouponPolicyResponse(
            entity.getId(),
            entity.getCode(),
            entity.getName(),
            entity.getDescription(),
            entity.getPlatformId(),
            entity.getType(),
            entity.getStatus(),
            CouponIssueForm.of(entity.getIssueCondition()),
            CouponBenefitForm.of(entity.getBenefitCondition()),
            CouponApplyForm.of(entity.getApplyCondition()),
            CouponUsageForm.of(entity.getUsageCondition()),
            CouponLifecycleForm.of(entity.getLifecycleCondition()),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedBy(),
            entity.getUpdatedAt()
        );
    }
}

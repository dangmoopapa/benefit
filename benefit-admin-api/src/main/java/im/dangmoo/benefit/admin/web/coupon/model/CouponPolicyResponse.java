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

    public static CouponPolicyResponse of(final CouponPolicy document) {
        return new CouponPolicyResponse(
            document.getId(),
            document.getCode(),
            document.getName(),
            document.getDescription(),
            document.getPlatformId(),
            document.getType(),
            document.getStatus(),
            CouponIssueForm.of(document.getIssueCondition()),
            CouponBenefitForm.of(document.getBenefitCondition()),
            CouponApplyForm.of(document.getApplyCondition()),
            CouponUsageForm.of(document.getUsageCondition()),
            CouponLifecycleForm.of(document.getLifecycleCondition()),
            document.getCreatedBy(),
            document.getCreatedAt(),
            document.getUpdatedBy(),
            document.getUpdatedAt()
        );
    }
}

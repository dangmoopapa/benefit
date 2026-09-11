package im.dangmoo.benefit.admin.web.coupon.model;

import im.dangmoo.benefit.admin.web.coupon.model.apply.CouponApplyForm;
import im.dangmoo.benefit.admin.web.coupon.model.benefit.CouponBenefitForm;
import im.dangmoo.benefit.admin.web.coupon.model.issue.CouponIssueForm;
import im.dangmoo.benefit.admin.web.coupon.model.lifecycle.CouponLifecycleForm;
import im.dangmoo.benefit.admin.web.coupon.model.usage.CouponUsageForm;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyType;

public record CouponPolicyRequest(
    String code,
    String name,
    String description,
    String platformId,
    CouponPolicyType type,
    CouponIssueForm issueCondition,
    CouponBenefitForm benefitCondition,
    CouponApplyForm applyCondition,
    CouponUsageForm usageCondition,
    CouponLifecycleForm lifecycleCondition
) {

    public CouponPolicy toEntity(final String createdBy) {
        return CouponPolicy.create(
            code,
            name,
            description,
            platformId,
            type,
            issueCondition.toEntity(),
            benefitCondition.toEntity(),
            applyCondition.toEntity(),
            usageCondition.toEntity(),
            lifecycleCondition.toEntity(),
            createdBy
        );
    }
}

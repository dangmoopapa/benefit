package im.dangmoo.benefit.admin.web.coupon.model;

import im.dangmoo.benefit.admin.web.coupon.model.apply.CouponApplyForm;
import im.dangmoo.benefit.admin.web.coupon.model.benefit.CouponBenefitForm;
import im.dangmoo.benefit.admin.web.coupon.model.issue.CouponIssueForm;
import im.dangmoo.benefit.admin.web.coupon.model.lifecycle.CouponLifecycleForm;
import im.dangmoo.benefit.admin.web.coupon.model.usage.CouponUsageForm;
import im.dangmoo.benefit.domain.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.domain.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.domain.coupon.policy.CouponPolicyType;

public record CouponPolicyRequest(
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
    CouponLifecycleForm lifecycleCondition
) {

    public CouponPolicy toDocument() {
        return CouponPolicy.create(
            code,
            name,
            description,
            platformId,
            type,
            issueCondition.toDocument(),
            benefitCondition.toDocument(),
            applyCondition.toDocument(),
            usageCondition.toDocument(),
            lifecycleCondition.toDocument()
        );
    }
}

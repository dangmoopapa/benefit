package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponAccountCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponApplyCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponBenefitCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueFrequency;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponLifecycleCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyType;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageValidityType;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

public record CouponPolicyCreateRequest(
    String name,
    String description,
    String key,
    CouponPolicyType type,
    String code,
    BenefitCondition benefitCondition,
    IssueCondition issueCondition,
    UsageCondition usageCondition,
    ApplyCondition applyCondition,
    LifecycleCondition lifecycleCondition,
    AccountCondition accountCondition
) {

    public CouponPolicyDocument toDocument(final String createdBy) {
        return CouponPolicyDocument.create(
            name,
            description,
            key,
            type,
            benefitCondition.toDocument(),
            issueCondition.toDocument(),
            usageCondition.toDocument(),
            applyCondition.toDocument(),
            lifecycleCondition.toDocument(),
            accountCondition.toDocument(),
            createdBy
        );
    }

    public record BenefitCondition(
        BigDecimal amount,
        BigDecimal rate,
        BigDecimal maxDiscountAmount
    ) {

        public CouponBenefitCondition toDocument() {
            return CouponBenefitCondition.create(amount, rate, maxDiscountAmount);
        }
    }

    public record IssueCondition(
        Instant startAt,
        Instant endAt,
        Long stockQuantity,
        List<DayOfWeek> availableDaysOfWeek,
        List<Integer> hours,
        CouponIssueFrequency frequency
    ) {

        public CouponIssueCondition toDocument() {
            return CouponIssueCondition.create(
                startAt,
                endAt,
                stockQuantity,
                availableDaysOfWeek,
                hours,
                frequency
            );
        }
    }

    public record UsageCondition(
        CouponUsageValidityType validityType,
        Instant startAt,
        Instant endAt,
        Integer daysAfterIssue,
        Long stockQuantity,
        BigDecimal minPaymentAmount
    ) {

        public CouponUsageCondition toDocument() {
            return CouponUsageCondition.create(
                validityType,
                startAt,
                endAt,
                daysAfterIssue,
                stockQuantity,
                minPaymentAmount
            );
        }
    }

    public record ApplyCondition(
        List<String> productIds,
        List<String> categoryIds,
        List<String> brandIds,
        String segmentId
    ) {

        public CouponApplyCondition toDocument() {
            return CouponApplyCondition.create(productIds, categoryIds, brandIds, segmentId);
        }
    }

    public record LifecycleCondition(
        boolean reclaimableOnPaymentCancel,
        boolean reclaimable
    ) {

        public CouponLifecycleCondition toDocument() {
            return CouponLifecycleCondition.create(reclaimableOnPaymentCancel, reclaimable);
        }
    }

    public record AccountCondition(String accountKey) {

        public CouponAccountCondition toDocument() {
            return CouponAccountCondition.create(accountKey);
        }
    }
}

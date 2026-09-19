package im.dangmoo.benefit.admin.model.coupon.policy;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponAccountCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponApplyCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponBenefitCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponLifecycleCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyType;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageValidityType;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

public record CouponPolicyDetailResponse(
    String id,
    String name,
    String description,
    String key,
    CouponPolicyType type,
    CouponPolicyStatus status,
    BenefitCondition benefitCondition,
    IssueCondition issueCondition,
    UsageCondition usageCondition,
    ApplyCondition applyCondition,
    LifecycleCondition lifecycleCondition,
    AccountCondition accountCondition,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static CouponPolicyDetailResponse of(final CouponPolicy policy) {
        return new CouponPolicyDetailResponse(
            policy.getId(),
            policy.getName(),
            policy.getDescription(),
            policy.getKey(),
            policy.getType(),
            policy.getStatus(),
            BenefitCondition.of(policy.getBenefitCondition()),
            IssueCondition.of(policy.getIssueCondition()),
            UsageCondition.of(policy.getUsageCondition()),
            ApplyCondition.of(policy.getApplyCondition()),
            LifecycleCondition.of(policy.getLifecycleCondition()),
            AccountCondition.of(policy.getAccountCondition()),
            policy.getCreatedBy(),
            policy.getCreatedAt(),
            policy.getUpdatedBy(),
            policy.getUpdatedAt()
        );
    }

    public record BenefitCondition(
        BigDecimal amount,
        BigDecimal rate,
        BigDecimal maxDiscountAmount
    ) {

        public static BenefitCondition of(final CouponBenefitCondition condition) {
            return new BenefitCondition(
                condition.getAmount(),
                condition.getRate(),
                condition.getMaxDiscountAmount()
            );
        }
    }

    public record IssueCondition(
        Instant startAt,
        Instant endAt,
        Long stockQuantity,
        List<DayOfWeek> availableDaysOfWeek,
        List<Integer> hours
    ) {

        public static IssueCondition of(final CouponIssueCondition condition) {
            return new IssueCondition(
                condition.getStartAt(),
                condition.getEndAt(),
                condition.getStockQuantity(),
                condition.getAvailableDaysOfWeek(),
                condition.getHours()
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

        public static UsageCondition of(final CouponUsageCondition condition) {
            return new UsageCondition(
                condition.getValidityType(),
                condition.getStartAt(),
                condition.getEndAt(),
                condition.getDaysAfterIssue(),
                condition.getStockQuantity(),
                condition.getMinPaymentAmount()
            );
        }
    }

    public record ApplyCondition(
        List<String> productIds,
        List<String> categoryIds,
        List<String> brandIds,
        String segmentId
    ) {

        public static ApplyCondition of(final CouponApplyCondition condition) {
            return new ApplyCondition(
                condition.getProductIds(),
                condition.getCategoryIds(),
                condition.getBrandIds(),
                condition.getSegmentId()
            );
        }
    }

    public record LifecycleCondition(
        boolean reclaimableOnPaymentCancel,
        boolean reclaimable
    ) {

        public static LifecycleCondition of(final CouponLifecycleCondition condition) {
            return new LifecycleCondition(
                condition.isReclaimableOnPaymentCancel(),
                condition.isReclaimable()
            );
        }
    }

    public record AccountCondition(String accountKey) {

        public static AccountCondition of(final CouponAccountCondition condition) {
            return new AccountCondition(condition.getAccountKey());
        }
    }
}

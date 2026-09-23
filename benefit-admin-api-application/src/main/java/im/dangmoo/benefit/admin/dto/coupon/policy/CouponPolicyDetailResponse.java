package im.dangmoo.benefit.admin.dto.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.code.CouponCodeDocument;
import im.dangmoo.benefit.data.entity.coupon.code.CouponCodeStatus;
import im.dangmoo.benefit.data.entity.coupon.code.CouponCodeType;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponAccountCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponApplyCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponBenefitCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponIssueCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponIssueFrequency;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponLifecycleCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyType;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponUsageCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponUsageValidityType;

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
    List<Code> codes,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static CouponPolicyDetailResponse of(
        final CouponPolicyDocument policy,
        final List<CouponCodeDocument> codes
    ) {
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
            codes.stream().map(Code::of).toList(),
            policy.getCreatedBy(),
            policy.getCreatedAt(),
            policy.getUpdatedBy(),
            policy.getUpdatedAt()
        );
    }

    public record Code(
        String id,
        String code,
        CouponCodeType type,
        CouponCodeStatus status,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
    ) {

        public static Code of(final CouponCodeDocument code) {
            return new Code(
                code.getId(),
                code.getCode(),
                code.getType(),
                code.getStatus(),
                code.getCreatedBy(),
                code.getCreatedAt(),
                code.getUpdatedBy(),
                code.getUpdatedAt()
            );
        }
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
        List<Integer> hours,
        CouponIssueFrequency frequency
    ) {

        public static IssueCondition of(final CouponIssueCondition condition) {
            return new IssueCondition(
                condition.getStartAt(),
                condition.getEndAt(),
                condition.getStockQuantity(),
                condition.getAvailableDaysOfWeek(),
                condition.getHours(),
                condition.getFrequency()
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

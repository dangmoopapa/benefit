package im.dangmoo.benefit.admin.model.point.policy;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointAccountCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitType;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitWeightOption;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireType;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointIssueCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointIssueFrequency;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointLifecycleCondition;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

public record PointPolicyDetailResponse(
    String id,
    String name,
    String description,
    String key,
    PointPolicyStatus status,
    BenefitCondition benefitCondition,
    IssueCondition issueCondition,
    ExpireCondition expireCondition,
    LifecycleCondition lifecycleCondition,
    AccountCondition accountCondition,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static PointPolicyDetailResponse of(final PointPolicy policy) {
        return new PointPolicyDetailResponse(
            policy.getId(),
            policy.getName(),
            policy.getDescription(),
            policy.getKey(),
            policy.getStatus(),
            BenefitCondition.of(policy.getBenefitCondition()),
            IssueCondition.of(policy.getIssueCondition()),
            ExpireCondition.of(policy.getExpireCondition()),
            LifecycleCondition.of(policy.getLifecycleCondition()),
            AccountCondition.of(policy.getAccountCondition()),
            policy.getCreatedBy(),
            policy.getCreatedAt(),
            policy.getUpdatedBy(),
            policy.getUpdatedAt()
        );
    }

    public record BenefitCondition(
        PointBenefitType type,
        Long amount,
        Long minAmount,
        Long maxAmount,
        List<Long> amounts,
        List<WeightOption> options
    ) {

        public static BenefitCondition of(final PointBenefitCondition condition) {
            return new BenefitCondition(
                condition.getType(),
                condition.getAmount(),
                condition.getMinAmount(),
                condition.getMaxAmount(),
                condition.getAmounts(),
                condition.getOptions().stream().map(WeightOption::of).toList()
            );
        }
    }

    public record WeightOption(long amount, long weight) {

        public static WeightOption of(final PointBenefitWeightOption option) {
            return new WeightOption(option.getAmount(), option.getWeight());
        }
    }

    public record IssueCondition(
        Instant startAt,
        Instant endAt,
        Long stockQuantity,
        List<DayOfWeek> availableDaysOfWeek,
        List<Integer> hours,
        PointIssueFrequency frequency
    ) {

        public static IssueCondition of(final PointIssueCondition condition) {
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

    public record ExpireCondition(
        PointExpireType type,
        Instant expiresAt,
        Integer daysAfterGrant
    ) {

        public static ExpireCondition of(final PointExpireCondition condition) {
            return new ExpireCondition(
                condition.getType(),
                condition.getExpiresAt(),
                condition.getDaysAfterGrant()
            );
        }
    }

    public record LifecycleCondition(boolean reclaimable) {

        public static LifecycleCondition of(final PointLifecycleCondition condition) {
            return new LifecycleCondition(condition.isReclaimable());
        }
    }

    public record AccountCondition(String accountKey) {

        public static AccountCondition of(final PointAccountCondition condition) {
            return new AccountCondition(condition.getAccountKey());
        }
    }
}

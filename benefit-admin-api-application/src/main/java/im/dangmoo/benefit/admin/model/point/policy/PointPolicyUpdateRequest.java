package im.dangmoo.benefit.admin.model.point.policy;

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

public record PointPolicyUpdateRequest(
    String name,
    String description,
    String key,
    BenefitCondition benefitCondition,
    IssueCondition issueCondition,
    ExpireCondition expireCondition,
    LifecycleCondition lifecycleCondition,
    AccountCondition accountCondition
) {

    public record BenefitCondition(
        PointBenefitType type,
        Long amount,
        Long minAmount,
        Long maxAmount,
        List<Long> amounts,
        List<WeightOption> options
    ) {

        public PointBenefitCondition toDocument() {
            return PointBenefitCondition.create(
                type,
                amount,
                minAmount,
                maxAmount,
                amounts,
                options == null
                    ? List.of()
                    : options.stream().map(WeightOption::toDocument).toList()
            );
        }
    }

    public record WeightOption(long amount, long weight) {

        public PointBenefitWeightOption toDocument() {
            return PointBenefitWeightOption.create(amount, weight);
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

        public PointIssueCondition toDocument() {
            return PointIssueCondition.create(
                startAt,
                endAt,
                stockQuantity,
                availableDaysOfWeek,
                hours,
                frequency
            );
        }
    }

    public record ExpireCondition(
        PointExpireType type,
        Instant expiresAt,
        Integer daysAfterGrant
    ) {

        public PointExpireCondition toDocument() {
            return PointExpireCondition.create(type, expiresAt, daysAfterGrant);
        }
    }

    public record LifecycleCondition(boolean reclaimable) {

        public PointLifecycleCondition toDocument() {
            return PointLifecycleCondition.create(reclaimable);
        }
    }

    public record AccountCondition(String accountKey) {

        public PointAccountCondition toDocument() {
            return PointAccountCondition.create(accountKey);
        }
    }
}

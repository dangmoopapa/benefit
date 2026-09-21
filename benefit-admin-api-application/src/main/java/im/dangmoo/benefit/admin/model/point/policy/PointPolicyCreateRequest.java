package im.dangmoo.benefit.admin.model.point.policy;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointAccountCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireType;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointIssueCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointLifecycleCondition;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

public record PointPolicyCreateRequest(
    String name,
    String description,
    String key,
    BenefitCondition benefitCondition,
    IssueCondition issueCondition,
    ExpireCondition expireCondition,
    LifecycleCondition lifecycleCondition,
    AccountCondition accountCondition
) {

    public PointPolicy toDocument(final String createdBy) {
        return PointPolicy.create(
            name,
            description,
            key,
            benefitCondition.toDocument(),
            issueCondition.toDocument(),
            expireCondition.toDocument(),
            lifecycleCondition.toDocument(),
            accountCondition.toDocument(),
            createdBy
        );
    }

    public record BenefitCondition(long amount) {

        public PointBenefitCondition toDocument() {
            return PointBenefitCondition.create(amount);
        }
    }

    public record IssueCondition(
        Instant startAt,
        Instant endAt,
        Long stockQuantity,
        List<DayOfWeek> availableDaysOfWeek,
        List<Integer> hours
    ) {

        public PointIssueCondition toDocument() {
            return PointIssueCondition.create(startAt, endAt, stockQuantity, availableDaysOfWeek, hours);
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

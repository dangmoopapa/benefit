package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyDocument;

import java.time.Instant;

public final class PromotionPolicyDomain {

    public enum Applicability {
        APPLICABLE,
        NOT_OPEN,
        ALREADY_APPLIED
    }

    private final boolean active;
    private final Instant startAt;
    private final Instant endAt;

    private PromotionPolicyDomain(
        final boolean active,
        final Instant startAt,
        final Instant endAt
    ) {
        this.active = active;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public static PromotionPolicyDomain of(final PromotionPolicyDocument policy) {
        return new PromotionPolicyDomain(
            policy.getStatus() == PromotionPolicyStatus.ACTIVE,
            policy.getStartAt(),
            policy.getEndAt()
        );
    }

    public boolean isOpenAt(final Instant now) {
        return active && !now.isBefore(startAt) && !now.isAfter(endAt);
    }

    public boolean isEndedAt(final Instant now) {
        return now.isAfter(endAt);
    }

    public Applicability applicabilityAt(final Instant now, final boolean alreadyApplied) {
        if (!isOpenAt(now)) {
            return Applicability.NOT_OPEN;
        }
        if (alreadyApplied) {
            return Applicability.ALREADY_APPLIED;
        }
        return Applicability.APPLICABLE;
    }
}

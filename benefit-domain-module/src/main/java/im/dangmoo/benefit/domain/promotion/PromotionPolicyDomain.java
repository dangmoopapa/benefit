package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeatureType;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;

import java.time.Instant;
import java.util.Optional;

public class PromotionPolicyDomain {

    private final boolean active;
    private final Instant startAt;
    private final Instant endAt;
    private final PromotionEntryDomain entry;

    private PromotionPolicyDomain(
        final boolean active,
        final Instant startAt,
        final Instant endAt,
        final PromotionEntryDomain entry
    ) {
        this.active = active;
        this.startAt = startAt;
        this.endAt = endAt;
        this.entry = entry;
    }

    public static PromotionPolicyDomain of(final PromotionPolicy policy) {
        return new PromotionPolicyDomain(
            policy.getStatus() == PromotionPolicyStatus.ACTIVE,
            policy.getStartAt(),
            policy.getEndAt(),
            findEntry(policy.getFeatures()).orElse(null)
        );
    }

    public boolean isInPeriod(final Instant now) {
        return !now.isBefore(startAt) && !now.isAfter(endAt);
    }

    public boolean isEnded(final Instant now) {
        return now.isAfter(endAt);
    }

    public boolean isLive(final Instant now) {
        return active && isInPeriod(now);
    }

    public Optional<PromotionEntryDomain> entry() {
        return Optional.ofNullable(entry);
    }

    public boolean hasEntry() {
        return entry != null;
    }

    public void requireApplicable(final Instant now, final boolean alreadyApplied) {
        if (!isLive(now) || entry == null) {
            throw new NotApplicableException();
        }
        entry.requireNotAlreadyApplied(alreadyApplied);
    }

    public void requireAutoLotteryReady(final Instant now, final boolean alreadyDrawn) {
        if (!isEnded(now) || entry == null) {
            throw new LotteryNotReadyException();
        }
        entry.requireAutoLotteryReady(alreadyDrawn);
    }

    private static Optional<PromotionEntryDomain> findEntry(final java.util.List<PromotionFeature> features) {
        if (features == null) {
            return Optional.empty();
        }
        return features.stream()
            .filter(feature -> feature.getType() == PromotionFeatureType.ENTRY)
            .map(PromotionFeature::getEntry)
            .map(PromotionEntryDomain::of)
            .findFirst();
    }

    public static class NotApplicableException extends RuntimeException {
    }

    public static class LotteryNotReadyException extends RuntimeException {
    }
}

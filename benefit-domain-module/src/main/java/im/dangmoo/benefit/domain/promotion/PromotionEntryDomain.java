package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.data.entity.promotion.feature.PromotionEntry;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionFeatureType;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionPrize;
import im.dangmoo.benefit.data.entity.promotion.policy.PromotionPolicyDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class PromotionEntryDomain {

    public enum Drawability {
        DRAWABLE,
        LOTTERY_TYPE_MISMATCH,
        ALREADY_DRAWN
    }

    private final PromotionLotteryType lotteryType;
    private final Integer winnerCount;
    private final List<PromotionPrize> prizes;

    private PromotionEntryDomain(
        final PromotionLotteryType lotteryType,
        final Integer winnerCount,
        final List<PromotionPrize> prizes
    ) {
        this.lotteryType = lotteryType;
        this.winnerCount = winnerCount;
        this.prizes = prizes;
    }

    public static Optional<PromotionEntryDomain> findIn(final PromotionPolicyDocument policy) {
        final List<PromotionFeature> features = policy.getFeatures();
        if (features == null) {
            return Optional.empty();
        }
        return features.stream()
            .filter(feature -> feature.getType() == PromotionFeatureType.ENTRY)
            .map(PromotionFeature::getEntry)
            .filter(Objects::nonNull)
            .map(PromotionEntryDomain::of)
            .findFirst();
    }

    public static PromotionEntryDomain of(final PromotionEntry entry) {
        return new PromotionEntryDomain(
            entry.getLotteryType(),
            entry.getWinnerCount(),
            entry.getPrizes() == null ? List.of() : List.copyOf(entry.getPrizes())
        );
    }

    public List<PromotionPrize> prizes() {
        return prizes;
    }

    public Drawability manualDrawability(final boolean alreadyDrawn) {
        return drawabilityBy(PromotionLotteryType.MANUAL, alreadyDrawn);
    }

    public Drawability autoDrawability(final boolean alreadyDrawn) {
        return drawabilityBy(PromotionLotteryType.AUTO_COUNT, alreadyDrawn);
    }

    public List<String> drawWinnersFrom(
        final List<String> candidateUserIds,
        final Set<String> alreadyWonUserIds
    ) {
        if (winnerCount == null || winnerCount <= 0) {
            return List.of();
        }
        final List<String> candidates = candidateUserIds.stream()
            .filter(candidateUserId -> !alreadyWonUserIds.contains(candidateUserId))
            .collect(Collectors.toCollection(ArrayList::new));
        if (candidates.isEmpty()) {
            return List.of();
        }

        Collections.shuffle(candidates, ThreadLocalRandom.current());
        return candidates.subList(0, Math.min(winnerCount, candidates.size()));
    }

    private Drawability drawabilityBy(final PromotionLotteryType drawnBy, final boolean alreadyDrawn) {
        if (lotteryType != drawnBy) {
            return Drawability.LOTTERY_TYPE_MISMATCH;
        }
        if (alreadyDrawn) {
            return Drawability.ALREADY_DRAWN;
        }
        return Drawability.DRAWABLE;
    }
}

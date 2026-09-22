package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionEntry;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PromotionEntryDomain {

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

    public static PromotionEntryDomain of(final PromotionEntry entry) {
        return new PromotionEntryDomain(
            entry.getLotteryType(),
            entry.getWinnerCount(),
            entry.getPrizes() == null ? List.of() : List.copyOf(entry.getPrizes())
        );
    }

    public PromotionLotteryType lotteryType() {
        return lotteryType;
    }

    public List<PromotionPrize> prizes() {
        return prizes;
    }

    public void requireNotAlreadyApplied(final boolean alreadyApplied) {
        if (alreadyApplied) {
            throw new AlreadyAppliedException();
        }
    }

    public void requireManualLotteryReady(final boolean alreadyDrawn) {
        if (lotteryType != PromotionLotteryType.MANUAL) {
            throw new LotteryNotReadyException();
        }
        if (alreadyDrawn) {
            throw new AlreadyDrawnException();
        }
    }

    public void requireAutoLotteryReady(final boolean alreadyDrawn) {
        if (lotteryType != PromotionLotteryType.AUTO_COUNT) {
            throw new LotteryNotReadyException();
        }
        if (alreadyDrawn) {
            throw new AlreadyDrawnException();
        }
    }

    public List<String> selectWinners(
        final List<String> candidateUserIds,
        final Set<String> alreadyWonUserIds
    ) {
        final List<String> candidates = candidateUserIds.stream()
            .filter(userId -> !alreadyWonUserIds.contains(userId))
            .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        if (candidates.isEmpty()) {
            return List.of();
        }
        if (winnerCount == null || winnerCount <= 0) {
            return List.of();
        }

        Collections.shuffle(candidates, ThreadLocalRandom.current());
        return candidates.subList(0, Math.min(winnerCount, candidates.size()));
    }

    public static class AlreadyAppliedException extends RuntimeException {
    }

    public static class LotteryNotReadyException extends RuntimeException {
    }

    public static class AlreadyDrawnException extends RuntimeException {
    }
}

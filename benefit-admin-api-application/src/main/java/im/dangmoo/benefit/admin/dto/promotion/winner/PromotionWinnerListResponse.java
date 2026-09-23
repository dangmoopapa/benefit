package im.dangmoo.benefit.admin.dto.promotion.winner;

import im.dangmoo.benefit.data.entity.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionPrize;
import im.dangmoo.benefit.data.entity.promotion.winner.PromotionWinnerDocument;

import java.time.Instant;
import java.util.List;

public record PromotionWinnerListResponse(List<Item> items) {

    public static PromotionWinnerListResponse of(final List<PromotionWinnerDocument> winners) {
        return new PromotionWinnerListResponse(
            winners.stream().map(Item::of).toList()
        );
    }

    public record Item(
        String id,
        String userId,
        PromotionLotteryType lotteryType,
        List<PromotionPrize> prizes,
        String drawnBy,
        Instant drawnAt
    ) {
        public static Item of(final PromotionWinnerDocument winner) {
            return new Item(
                winner.getId(),
                winner.getUserId(),
                winner.getLotteryType(),
                winner.getPrizes(),
                winner.getDrawnBy(),
                winner.getDrawnAt()
            );
        }
    }
}

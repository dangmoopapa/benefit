package im.dangmoo.benefit.infrastructure.data.promotion.feature;

import java.util.ArrayList;
import java.util.List;

public class PromotionEntry {

    private String buttonLabel;
    private PromotionLotteryType lotteryType;
    private Integer winnerCount;
    private List<PromotionPrize> prizes = new ArrayList<>();

    private PromotionEntry() {
    }

    public static PromotionEntry create(
        final String buttonLabel,
        final PromotionLotteryType lotteryType,
        final Integer winnerCount,
        final List<PromotionPrize> prizes
    ) {
        final PromotionEntry entry = new PromotionEntry();
        entry.buttonLabel = buttonLabel;
        entry.lotteryType = lotteryType;
        entry.winnerCount = winnerCount;
        entry.prizes = prizes == null ? new ArrayList<>() : new ArrayList<>(prizes);
        return entry;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public PromotionLotteryType getLotteryType() {
        return lotteryType;
    }

    public Integer getWinnerCount() {
        return winnerCount;
    }

    public List<PromotionPrize> getPrizes() {
        return prizes;
    }
}

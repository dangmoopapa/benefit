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

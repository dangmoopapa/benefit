package im.dangmoo.benefit.admin.model.promotion.winner;

import java.util.List;

public record PromotionDrawRequest(
    List<String> userIds
) {
}

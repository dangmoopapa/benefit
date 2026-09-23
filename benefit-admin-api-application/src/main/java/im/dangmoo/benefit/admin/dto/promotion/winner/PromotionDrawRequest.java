package im.dangmoo.benefit.admin.dto.promotion.winner;

import java.util.List;

public record PromotionDrawRequest(
    List<String> userIds
) {
}

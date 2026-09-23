package im.dangmoo.benefit.api.dto.membership;

import java.math.BigDecimal;

public record MembershipBenefitApplyRequest(
    String orderId,
    BigDecimal paymentAmount,
    String categoryId
) {
}

package im.dangmoo.benefit.api.model.membership;

import java.math.BigDecimal;

public record MembershipBenefitApplyRequest(
    String orderId,
    BigDecimal paymentAmount,
    String categoryId
) {
}

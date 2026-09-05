package im.dangmoo.benefit.api.web.coupon.model;

import java.math.BigDecimal;

public record CouponWalletUseRequest(
    String orderId,
    BigDecimal usedAmount
) {
}

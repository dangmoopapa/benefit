package im.dangmoo.benefit.admin.web.coupon.model;

import java.math.BigDecimal;

public record CouponWalletUseRequest(
    String orderId,
    BigDecimal usedAmount
) {
}

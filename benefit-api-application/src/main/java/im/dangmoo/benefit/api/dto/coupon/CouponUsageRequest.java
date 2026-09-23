package im.dangmoo.benefit.api.dto.coupon;

import java.math.BigDecimal;

public record CouponUsageRequest(
    String walletId,
    String orderId,
    BigDecimal usedAmount,
    BigDecimal paymentAmount,
    String productId,
    String categoryId,
    String brandId,
    String segmentId
) {
}

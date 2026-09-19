package im.dangmoo.benefit.admin.model.coupon.wallet;

import java.math.BigDecimal;

public record CouponWalletUsageRequest(
    String orderId,
    BigDecimal usedAmount,
    BigDecimal paymentAmount,
    String productId,
    String categoryId,
    String brandId,
    String segmentId
) {
}

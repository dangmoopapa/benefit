package im.dangmoo.benefit.api.model.coupon;

import java.math.BigDecimal;

public record CouponUsableRequest(
    BigDecimal paymentAmount,
    String productId,
    String categoryId,
    String brandId,
    String segmentId
) {
}

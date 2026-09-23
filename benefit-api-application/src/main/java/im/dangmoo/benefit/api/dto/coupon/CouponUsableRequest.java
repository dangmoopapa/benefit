package im.dangmoo.benefit.api.dto.coupon;

import java.math.BigDecimal;

public record CouponUsableRequest(
    BigDecimal paymentAmount,
    String productId,
    String categoryId,
    String brandId,
    String segmentId
) {
}

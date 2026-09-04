package im.dangmoo.benefit.domain.coupon.policy.usage;

import java.math.BigDecimal;

public record CouponOrderSnapshot(
    BigDecimal amount,
    String paymentMethod,
    String shippingMethod,
    String region,
    boolean firstPurchase,
    int purchaseCount
) {
}

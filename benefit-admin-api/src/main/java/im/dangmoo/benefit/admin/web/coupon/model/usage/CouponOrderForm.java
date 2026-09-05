package im.dangmoo.benefit.admin.web.coupon.model.usage;

import im.dangmoo.benefit.domain.data.coupon.policy.usage.CouponOrderCondition;

import java.math.BigDecimal;
import java.util.List;

public record CouponOrderForm(
    BigDecimal minAmount,
    BigDecimal maxAmount,
    List<String> paymentMethods,
    List<String> shippingMethods,
    List<String> regions,
    boolean firstPurchaseOnly,
    Integer minPurchaseCount
) {

    public CouponOrderCondition toDocument() {
        return CouponOrderCondition.create(
            minAmount,
            maxAmount,
            paymentMethods,
            shippingMethods,
            regions,
            firstPurchaseOnly,
            minPurchaseCount
        );
    }

    public static CouponOrderForm of(final CouponOrderCondition document) {
        return new CouponOrderForm(
            document.getMinAmount(),
            document.getMaxAmount(),
            document.getPaymentMethods(),
            document.getShippingMethods(),
            document.getRegions(),
            document.isFirstPurchaseOnly(),
            document.getMinPurchaseCount()
        );
    }
}

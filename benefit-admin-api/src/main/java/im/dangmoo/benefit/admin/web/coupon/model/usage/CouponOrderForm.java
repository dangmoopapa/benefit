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

    public CouponOrderCondition toEntity() {
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

    public static CouponOrderForm of(final CouponOrderCondition entity) {
        return new CouponOrderForm(
            entity.getMinAmount(),
            entity.getMaxAmount(),
            entity.getPaymentMethods(),
            entity.getShippingMethods(),
            entity.getRegions(),
            entity.isFirstPurchaseOnly(),
            entity.getMinPurchaseCount()
        );
    }
}

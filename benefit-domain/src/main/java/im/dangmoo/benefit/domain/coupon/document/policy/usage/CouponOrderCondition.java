package im.dangmoo.benefit.domain.coupon.document.policy.usage;

import java.math.BigDecimal;
import java.util.List;

public class CouponOrderCondition {

    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private List<String> paymentMethods;
    private List<String> shippingMethods;
    private List<String> regions;
    private boolean firstPurchaseOnly;
    private Integer minPurchaseCount;

    private CouponOrderCondition() {
    }

    public static CouponOrderCondition create(
        final BigDecimal minAmount,
        final BigDecimal maxAmount,
        final List<String> paymentMethods,
        final List<String> shippingMethods,
        final List<String> regions,
        final boolean firstPurchaseOnly,
        final Integer minPurchaseCount
    ) {
        final CouponOrderCondition document = new CouponOrderCondition();
        document.minAmount = minAmount;
        document.maxAmount = maxAmount;
        document.paymentMethods = paymentMethods;
        document.shippingMethods = shippingMethods;
        document.regions = regions;
        document.firstPurchaseOnly = firstPurchaseOnly;
        document.minPurchaseCount = minPurchaseCount;
        return document;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public List<String> getPaymentMethods() {
        return paymentMethods;
    }

    public List<String> getShippingMethods() {
        return shippingMethods;
    }

    public List<String> getRegions() {
        return regions;
    }

    public boolean isFirstPurchaseOnly() {
        return firstPurchaseOnly;
    }

    public Integer getMinPurchaseCount() {
        return minPurchaseCount;
    }
}

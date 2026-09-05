package im.dangmoo.benefit.domain.coupon.document.policy.benefit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class CouponBenefitCondition {

    private CouponBenefitType type;
    private BigDecimal value;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minPaymentAmount;

    private CouponBenefitCondition() {
    }

    public static CouponBenefitCondition create(
        final CouponBenefitType type,
        final BigDecimal value,
        final BigDecimal maxDiscountAmount,
        final BigDecimal minPaymentAmount
    ) {
        final CouponBenefitCondition document = new CouponBenefitCondition();
        document.type = type;
        document.value = value;
        document.maxDiscountAmount = maxDiscountAmount;
        document.minPaymentAmount = minPaymentAmount;
        return document;
    }

    public CouponBenefitType getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public BigDecimal getMinPaymentAmount() {
        return minPaymentAmount;
    }

    public boolean isApplicableTo(final BigDecimal paymentAmount) {
        return minPaymentAmount == null || paymentAmount.compareTo(minPaymentAmount) >= 0;
    }

    public Optional<BigDecimal> calculateDiscount(final BigDecimal baseAmount) {
        if (!isApplicableTo(baseAmount)) {
            return Optional.empty();
        }
        BigDecimal discount = switch (type) {
            case AMOUNT -> value;
            case RATE -> baseAmount.multiply(value).setScale(0, RoundingMode.DOWN);
        };
        if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
            discount = maxDiscountAmount;
        }
        if (discount.compareTo(baseAmount) > 0) {
            discount = baseAmount;
        }
        return Optional.of(discount);
    }
}

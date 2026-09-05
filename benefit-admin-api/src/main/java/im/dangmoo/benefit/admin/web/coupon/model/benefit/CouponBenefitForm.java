package im.dangmoo.benefit.admin.web.coupon.model.benefit;

import im.dangmoo.benefit.domain.coupon.document.policy.benefit.CouponBenefitCondition;
import im.dangmoo.benefit.domain.coupon.document.policy.benefit.CouponBenefitType;

import java.math.BigDecimal;

public record CouponBenefitForm(
    CouponBenefitType type,
    BigDecimal value,
    BigDecimal maxDiscountAmount,
    BigDecimal minPaymentAmount
) {

    public CouponBenefitCondition toDocument() {
        return CouponBenefitCondition.create(type, value, maxDiscountAmount, minPaymentAmount);
    }

    public static CouponBenefitForm of(final CouponBenefitCondition document) {
        return new CouponBenefitForm(
            document.getType(),
            document.getValue(),
            document.getMaxDiscountAmount(),
            document.getMinPaymentAmount()
        );
    }
}

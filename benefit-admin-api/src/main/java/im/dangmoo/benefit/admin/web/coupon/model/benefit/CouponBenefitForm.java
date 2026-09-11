package im.dangmoo.benefit.admin.web.coupon.model.benefit;

import im.dangmoo.benefit.domain.data.coupon.policy.benefit.CouponBenefitCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.benefit.CouponBenefitType;

import java.math.BigDecimal;

public record CouponBenefitForm(
    CouponBenefitType type,
    BigDecimal value,
    BigDecimal maxDiscountAmount,
    BigDecimal minPaymentAmount
) {

    public CouponBenefitCondition toEntity() {
        return CouponBenefitCondition.create(type, value, maxDiscountAmount, minPaymentAmount);
    }

    public static CouponBenefitForm of(final CouponBenefitCondition entity) {
        return new CouponBenefitForm(
            entity.getType(),
            entity.getValue(),
            entity.getMaxDiscountAmount(),
            entity.getMinPaymentAmount()
        );
    }
}

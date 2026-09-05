package im.dangmoo.benefit.domain.coupon.policy.benefit;

import im.dangmoo.benefit.domain.coupon.document.policy.benefit.CouponBenefitCondition;
import im.dangmoo.benefit.domain.coupon.document.policy.benefit.CouponBenefitType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CouponBenefitConditionTest {

    @Test
    @DisplayName("최소 결제금액 미만이면 적용 불가이다")
    void notApplicableBelowMinPayment() {
        final CouponBenefitCondition condition = CouponBenefitCondition.create(
            CouponBenefitType.AMOUNT,
            new BigDecimal("3000"),
            null,
            new BigDecimal("10000")
        );

        assertThat(condition.isApplicableTo(new BigDecimal("9999"))).isFalse();
        assertThat(condition.calculateDiscount(new BigDecimal("9999"))).isEmpty();
    }

    @Test
    @DisplayName("정액 할인은 금액 그대로 적용한다")
    void calculatesAmountDiscount() {
        final CouponBenefitCondition condition = CouponBenefitCondition.create(
            CouponBenefitType.AMOUNT,
            new BigDecimal("3000"),
            null,
            new BigDecimal("10000")
        );

        assertThat(condition.calculateDiscount(new BigDecimal("15000")))
            .contains(new BigDecimal("3000"));
    }

    @Test
    @DisplayName("정률 할인은 상한과 결제액을 넘지 않는다")
    void calculatesRateDiscountWithCap() {
        final CouponBenefitCondition condition = CouponBenefitCondition.create(
            CouponBenefitType.RATE,
            new BigDecimal("0.5"),
            new BigDecimal("4000"),
            null
        );

        assertThat(condition.calculateDiscount(new BigDecimal("10000")))
            .contains(new BigDecimal("4000"));
    }

    @Test
    @DisplayName("할인액이 결제액보다 크면 결제액으로 자른다")
    void capsDiscountAtBaseAmount() {
        final CouponBenefitCondition condition = CouponBenefitCondition.create(
            CouponBenefitType.AMOUNT,
            new BigDecimal("5000"),
            null,
            null
        );

        assertThat(condition.calculateDiscount(new BigDecimal("3000")))
            .contains(new BigDecimal("3000"));
    }
}

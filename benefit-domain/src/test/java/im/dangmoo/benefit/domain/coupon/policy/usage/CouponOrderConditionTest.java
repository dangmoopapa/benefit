package im.dangmoo.benefit.domain.coupon.policy.usage;

import im.dangmoo.benefit.domain.coupon.document.policy.usage.CouponOrderCondition;
import im.dangmoo.benefit.domain.coupon.document.policy.usage.CouponOrderSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponOrderConditionTest {

    @Test
    @DisplayName("최소 금액 미만이면 실패한다")
    void failsBelowMinAmount() {
        final CouponOrderCondition condition = CouponOrderCondition.create(
            new BigDecimal("10000"),
            null,
            List.of(),
            List.of(),
            List.of(),
            false,
            null
        );

        assertThat(condition.isSatisfiedBy(
            new CouponOrderSnapshot(new BigDecimal("9999"), "CARD", "NORMAL", "KR", false, 1)
        )).isFalse();
    }

    @Test
    @DisplayName("허용 결제수단이 아니면 실패한다")
    void failsOnDisallowedPaymentMethod() {
        final CouponOrderCondition condition = CouponOrderCondition.create(
            null,
            null,
            List.of("CARD"),
            List.of(),
            List.of(),
            false,
            null
        );

        assertThat(condition.isSatisfiedBy(
            new CouponOrderSnapshot(new BigDecimal("10000"), "POINT", "NORMAL", "KR", false, 1)
        )).isFalse();
    }

    @Test
    @DisplayName("첫구매 전용인데 첫구매가 아니면 실패한다")
    void failsWhenNotFirstPurchase() {
        final CouponOrderCondition condition = CouponOrderCondition.create(
            null,
            null,
            List.of(),
            List.of(),
            List.of(),
            true,
            null
        );

        assertThat(condition.isSatisfiedBy(
            new CouponOrderSnapshot(new BigDecimal("10000"), "CARD", "NORMAL", "KR", false, 2)
        )).isFalse();
    }

    @Test
    @DisplayName("주문 조건이 모두 맞으면 통과한다")
    void passesWhenOrderMatches() {
        final CouponOrderCondition condition = CouponOrderCondition.create(
            new BigDecimal("10000"),
            new BigDecimal("50000"),
            List.of("CARD"),
            List.of("NORMAL"),
            List.of("KR"),
            true,
            1
        );

        assertThat(condition.isSatisfiedBy(
            new CouponOrderSnapshot(new BigDecimal("20000"), "CARD", "NORMAL", "KR", true, 1)
        )).isTrue();
    }
}

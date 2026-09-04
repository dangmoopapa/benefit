package im.dangmoo.benefit.domain.coupon.policy.usage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouponStackingConditionTest {

    @Test
    @DisplayName("다른 쿠폰과 불가인데 함께면 실패한다")
    void failsWhenOtherCouponNotCombinable() {
        final CouponStackingCondition condition = CouponStackingCondition.create(
            false, true, true, true, true, 0, false
        );

        assertThat(condition.isSatisfiedBy(new CouponStackingSnapshot(true, false, false, false, false)))
            .isFalse();
    }

    @Test
    @DisplayName("포인트와 불가인데 함께면 실패한다")
    void failsWhenPointNotCombinable() {
        final CouponStackingCondition condition = CouponStackingCondition.create(
            true, true, false, true, true, 0, false
        );

        assertThat(condition.isSatisfiedBy(new CouponStackingSnapshot(false, false, true, false, false)))
            .isFalse();
    }

    @Test
    @DisplayName("허용된 조합이면 통과한다")
    void passesWhenCombinable() {
        final CouponStackingCondition condition = CouponStackingCondition.create(
            true, true, true, false, false, 1, true
        );

        assertThat(condition.isSatisfiedBy(new CouponStackingSnapshot(true, true, true, false, false)))
            .isTrue();
    }
}

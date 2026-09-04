package im.dangmoo.benefit.domain.coupon.policy.usage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouponUsageLimitTest {

    @Test
    @DisplayName("유저당 한도에 도달하면 실패한다")
    void failsWhenPerUserExhausted() {
        final CouponUsageLimit limit = CouponUsageLimit.create(2, null, null, null, null, null);

        assertThat(limit.isSatisfiedBy(new CouponUsageCountSnapshot(2, 0, 0, 0, 0, 0))).isFalse();
    }

    @Test
    @DisplayName("전체 한도 미만이면 통과한다")
    void passesWhenBelowTotal() {
        final CouponUsageLimit limit = CouponUsageLimit.create(null, null, null, 100L, null, null);

        assertThat(limit.isSatisfiedBy(new CouponUsageCountSnapshot(0, 0, 0, 99, 0, 0))).isTrue();
    }

    @Test
    @DisplayName("시간당 한도에 도달하면 실패한다")
    void failsWhenPerHourExhausted() {
        final CouponUsageLimit limit = CouponUsageLimit.create(null, null, null, null, null, 3);

        assertThat(limit.isSatisfiedBy(new CouponUsageCountSnapshot(0, 0, 0, 0, 0, 3))).isFalse();
    }
}

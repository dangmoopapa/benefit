package im.dangmoo.benefit.domain.coupon.policy.usage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CouponUsageValidityTest {

    private static final Instant ISSUED_AT = Instant.parse("2026-03-15T10:30:00Z");
    private static final Instant FIXED_END = Instant.parse("2026-04-30T23:59:59Z");

    @Test
    @DisplayName("고정 기간이면 종료 시각을 만료로 쓴다")
    void expiresAtFixedPeriodEnd() {
        final CouponUsageValidity validity = CouponUsageValidity.create(
            CouponUsageValidityType.FIXED_PERIOD,
            Instant.parse("2026-03-01T00:00:00Z"),
            FIXED_END,
            null,
            null
        );

        assertThat(validity.resolveExpiresAt(ISSUED_AT)).isEqualTo(FIXED_END);
    }

    @Test
    @DisplayName("자정 기준이면 발급일 + N일 0시를 만료로 쓴다")
    void expiresAtMidnightAfterDays() {
        final CouponUsageValidity validity = CouponUsageValidity.create(
            CouponUsageValidityType.UNTIL_MIDNIGHT,
            null,
            null,
            7,
            null
        );

        assertThat(validity.resolveExpiresAt(ISSUED_AT))
            .isEqualTo(Instant.parse("2026-03-22T00:00:00Z"));
    }

    @Test
    @DisplayName("기간형이면 발급 시각에 일/시간을 더한다")
    void expiresAfterDuration() {
        final CouponUsageValidity validity = CouponUsageValidity.create(
            CouponUsageValidityType.DURATION,
            null,
            null,
            1,
            2
        );

        assertThat(validity.resolveExpiresAt(ISSUED_AT))
            .isEqualTo(Instant.parse("2026-03-16T12:30:00Z"));
    }

    @Test
    @DisplayName("구매 후 유효면 발급 시점 만료는 없다")
    void hasNoExpiryUntilPurchase() {
        final CouponUsageValidity validity = CouponUsageValidity.create(
            CouponUsageValidityType.AFTER_PURCHASE,
            null,
            null,
            null,
            null
        );

        assertThat(validity.resolveExpiresAt(ISSUED_AT)).isNull();
    }

    @Test
    @DisplayName("고정 기간 시작 전이면 비활성이다")
    void inactiveBeforeFixedPeriodStart() {
        final CouponUsageValidity validity = CouponUsageValidity.create(
            CouponUsageValidityType.FIXED_PERIOD,
            Instant.parse("2026-03-01T00:00:00Z"),
            FIXED_END,
            null,
            null
        );

        assertThat(validity.isActiveAt(ISSUED_AT, Instant.parse("2026-02-28T23:59:59Z"))).isFalse();
    }

    @Test
    @DisplayName("고정 기간 안이면 활성이다")
    void activeInsideFixedPeriod() {
        final CouponUsageValidity validity = CouponUsageValidity.create(
            CouponUsageValidityType.FIXED_PERIOD,
            Instant.parse("2026-03-01T00:00:00Z"),
            FIXED_END,
            null,
            null
        );

        assertThat(validity.isActiveAt(ISSUED_AT, Instant.parse("2026-03-20T12:00:00Z"))).isTrue();
    }

    @Test
    @DisplayName("만료 시각을 지나면 비활성이다")
    void inactiveAfterExpiry() {
        final CouponUsageValidity validity = CouponUsageValidity.create(
            CouponUsageValidityType.DURATION,
            null,
            null,
            1,
            null
        );

        assertThat(validity.isActiveAt(ISSUED_AT, Instant.parse("2026-03-16T10:30:01Z"))).isFalse();
    }

    @Test
    @DisplayName("구매 후 유효면 만료 없이 활성이다")
    void alwaysActiveUntilPurchase() {
        final CouponUsageValidity validity = CouponUsageValidity.create(
            CouponUsageValidityType.AFTER_PURCHASE,
            null,
            null,
            null,
            null
        );

        assertThat(validity.isActiveAt(ISSUED_AT, Instant.parse("2030-01-01T00:00:00Z"))).isTrue();
    }
}

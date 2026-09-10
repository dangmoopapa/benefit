package im.dangmoo.benefit.domain.component.coupon.policy.usage;

import im.dangmoo.benefit.domain.data.coupon.policy.usage.CouponUsageExpiration;
import im.dangmoo.benefit.domain.data.coupon.policy.usage.CouponUsageExpirationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CouponUsageExpirationTest {

    private static final Instant ISSUED_AT = Instant.parse("2026-03-15T10:30:00Z");
    private static final Instant FIXED_END = Instant.parse("2026-04-30T23:59:59Z");

    @Test
    @DisplayName("고정 기간이면 종료 시각을 만료로 쓴다")
    void expiresAtFixedPeriodEnd() {
        final CouponUsageExpiration usageExpiration = CouponUsageExpiration.create(
            CouponUsageExpirationType.FIXED_PERIOD,
            Instant.parse("2026-03-01T00:00:00Z"),
            FIXED_END,
            null,
            null
        );

        assertThat(usageExpiration.resolveExpiresAt(ISSUED_AT)).isEqualTo(FIXED_END);
    }

    @Test
    @DisplayName("자정 기준이면 발급일 + N일 0시를 만료로 쓴다")
    void expiresAtMidnightAfterDays() {
        final CouponUsageExpiration usageExpiration = CouponUsageExpiration.create(
            CouponUsageExpirationType.UNTIL_MIDNIGHT,
            null,
            null,
            7,
            null
        );

        assertThat(usageExpiration.resolveExpiresAt(ISSUED_AT))
            .isEqualTo(Instant.parse("2026-03-22T00:00:00Z"));
    }

    @Test
    @DisplayName("기간형이면 발급 시각에 일/시간을 더한다")
    void expiresAfterDuration() {
        final CouponUsageExpiration usageExpiration = CouponUsageExpiration.create(
            CouponUsageExpirationType.DURATION,
            null,
            null,
            1,
            2
        );

        assertThat(usageExpiration.resolveExpiresAt(ISSUED_AT))
            .isEqualTo(Instant.parse("2026-03-16T12:30:00Z"));
    }

    @Test
    @DisplayName("구매 후 유효면 발급 시점 만료는 없다")
    void hasNoExpiryUntilPurchase() {
        final CouponUsageExpiration usageExpiration = CouponUsageExpiration.create(
            CouponUsageExpirationType.AFTER_PURCHASE,
            null,
            null,
            null,
            null
        );

        assertThat(usageExpiration.resolveExpiresAt(ISSUED_AT)).isNull();
    }
}

package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponUsageCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponUsageValidityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CouponUsageDomainTest {

    private static final Instant ISSUED_AT = Instant.parse("2026-06-01T00:00:00Z");
    private static final Instant NOW = Instant.parse("2026-06-05T00:00:00Z");

    @Test
    @DisplayName("사용 기간 조건이 없으면 만료일이 없다")
    void expiresAtFrom_noValidity() {
        final CouponUsageDomain couponUsage = usage(usageCondition(null, null, null, null, null, null));
        assertThat(couponUsage.expiresAtFrom(ISSUED_AT)).isNull();
    }

    @Test
    @DisplayName("고정 기간이면 종료일이 만료일이다")
    void expiresAtFrom_fixedPeriod() {
        final Instant endAt = Instant.parse("2026-06-30T00:00:00Z");
        final CouponUsageDomain couponUsage = usage(usageCondition(
            CouponUsageValidityType.FIXED_PERIOD, ISSUED_AT, endAt, null, null, null
        ));
        assertThat(couponUsage.expiresAtFrom(ISSUED_AT)).isEqualTo(endAt);
    }

    @Test
    @DisplayName("발급 후 N일이면 발급일에 N일을 더한 날이 만료일이다")
    void expiresAtFrom_daysAfterIssue() {
        final CouponUsageDomain couponUsage = usage(usageCondition(
            CouponUsageValidityType.FOR_DAYS_AFTER_ISSUE, null, null, 7, null, null
        ));
        assertThat(couponUsage.expiresAtFrom(ISSUED_AT)).isEqualTo(Instant.parse("2026-06-08T00:00:00Z"));
    }

    @Test
    @DisplayName("발급 후 N일 조건인데 일수가 없으면 만료일이 없다")
    void expiresAtFrom_daysAfterIssueWithoutDays() {
        final CouponUsageDomain couponUsage = usage(usageCondition(
            CouponUsageValidityType.FOR_DAYS_AFTER_ISSUE, null, null, null, null, null
        ));
        assertThat(couponUsage.expiresAtFrom(ISSUED_AT)).isNull();
    }

    @Test
    @DisplayName("조건이 없으면 사용할 수 있다")
    void isUsableAt_unrestricted() {
        final CouponUsageDomain couponUsage = usage(usageCondition(null, null, null, null, null, null));
        assertThat(usable(couponUsage, NOW, null, BigDecimal.TEN)).isTrue();
    }

    @Test
    @DisplayName("만료일이 지났으면 사용할 수 없다")
    void isUsableAt_expired() {
        final CouponUsageDomain couponUsage = usage(usageCondition(null, null, null, null, null, null));
        assertThat(usable(couponUsage, NOW, NOW.minusSeconds(1), BigDecimal.TEN)).isFalse();
        assertThat(usable(couponUsage, NOW, NOW, BigDecimal.TEN)).isTrue();
    }

    @Test
    @DisplayName("고정 기간 밖이면 사용할 수 없다")
    void isUsableAt_outOfFixedPeriod() {
        final Instant startAt = Instant.parse("2026-06-10T00:00:00Z");
        final Instant endAt = Instant.parse("2026-06-20T00:00:00Z");
        final CouponUsageDomain couponUsage = usage(usageCondition(
            CouponUsageValidityType.FIXED_PERIOD, startAt, endAt, null, null, null
        ));
        assertThat(usable(couponUsage, startAt.minusSeconds(1), null, BigDecimal.TEN)).isFalse();
        assertThat(usable(couponUsage, startAt, null, BigDecimal.TEN)).isTrue();
        assertThat(usable(couponUsage, endAt, null, BigDecimal.TEN)).isTrue();
        assertThat(usable(couponUsage, endAt.plusSeconds(1), null, BigDecimal.TEN)).isFalse();
    }

    @Test
    @DisplayName("발급 후 N일이 지나면 사용할 수 없다")
    void isUsableAt_afterDaysFromIssue() {
        final CouponUsageDomain couponUsage = usage(usageCondition(
            CouponUsageValidityType.UNTIL_DAYS_AFTER_ISSUE, null, null, 3, null, null
        ));
        assertThat(usable(couponUsage, Instant.parse("2026-06-04T00:00:00Z"), null, BigDecimal.TEN)).isTrue();
        assertThat(usable(couponUsage, Instant.parse("2026-06-04T00:00:01Z"), null, BigDecimal.TEN)).isFalse();
    }

    @Test
    @DisplayName("사용 재고가 남지 않았으면 사용할 수 없다")
    void isUsableAt_stockExhausted() {
        final CouponUsageDomain couponUsage = usage(usageCondition(null, null, null, null, 5L, null));
        assertThat(couponUsage.isUsableAt(NOW, ISSUED_AT, null, 4L, BigDecimal.TEN))
            .isTrue();
        assertThat(couponUsage.isUsableAt(NOW, ISSUED_AT, null, 5L, BigDecimal.TEN))
            .isFalse();
    }

    @Test
    @DisplayName("최소 결제 금액에 못 미치면 사용할 수 없다")
    void isUsableAt_belowMinPaymentAmount() {
        final CouponUsageDomain couponUsage = usage(usageCondition(
            null, null, null, null, null, BigDecimal.valueOf(10_000)
        ));
        assertThat(usable(couponUsage, NOW, null, BigDecimal.valueOf(9_999))).isFalse();
        assertThat(usable(couponUsage, NOW, null, BigDecimal.valueOf(10_000))).isTrue();
        assertThat(usable(couponUsage, NOW, null, null)).isFalse();
    }

    private static boolean usable(
        final CouponUsageDomain couponUsage,
        final Instant now,
        final Instant expiresAt,
        final BigDecimal paymentAmount
    ) {
        return couponUsage.isUsableAt(now, ISSUED_AT, expiresAt, 0L, paymentAmount);
    }

    private static CouponUsageDomain usage(final CouponUsageCondition usageCondition) {
        return CouponUsageDomain.of(usageCondition);
    }

    private static CouponUsageCondition usageCondition(
        final CouponUsageValidityType validityType,
        final Instant startAt,
        final Instant endAt,
        final Integer daysAfterIssue,
        final Long stockQuantity,
        final BigDecimal minPaymentAmount
    ) {
        return CouponUsageCondition.create(
            validityType, startAt, endAt, daysAfterIssue, stockQuantity, minPaymentAmount
        );
    }
}

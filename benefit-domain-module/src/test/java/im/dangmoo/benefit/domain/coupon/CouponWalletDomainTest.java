package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueFrequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class CouponWalletDomainTest {

    private static final ZoneId UTC = ZoneOffset.UTC;

    @ParameterizedTest
    @CsvSource({
        "policy-1, user-1, policy-1:user-1",
        "abc, xyz, abc:xyz",
        "p, u, p:u"
    })
    @DisplayName("ONCE_PER_USER 는 policyId:userId 형식이다")
    void oncePerUser_format(final String policyId, final String userId, final String expected) {
        assertThat(CouponWalletDomain.idempotencyKey(
            policyId,
            userId,
            CouponIssueFrequency.ONCE_PER_USER,
            Instant.parse("2026-09-23T00:00:00Z")
        )).isEqualTo(expected);
    }

    @Test
    @DisplayName("frequency 가 null 이면 ONCE_PER_USER 로 취급한다")
    void nullFrequency_defaultsToOncePerUser() {
        assertThat(CouponWalletDomain.idempotencyKey("p", "u", null, Instant.parse("2026-01-01T00:00:00Z")))
            .isEqualTo("p:u");
    }

    @Test
    @DisplayName("ONCE_PER_DAY 는 UTC yyyy-MM-dd 를 붙인다")
    void oncePerDay_format() {
        final Instant at = LocalDateTime.of(2026, 9, 23, 12, 0).atZone(UTC).toInstant();
        assertThat(CouponWalletDomain.idempotencyKey("p1", "u1", CouponIssueFrequency.ONCE_PER_DAY, at))
            .isEqualTo("p1:u1:2026-09-23");
    }

    @Test
    @DisplayName("ONCE_PER_MONTH 는 UTC yyyy-MM 를 붙인다")
    void oncePerMonth_format() {
        final Instant at = LocalDateTime.of(2026, 9, 23, 12, 0).atZone(UTC).toInstant();
        assertThat(CouponWalletDomain.idempotencyKey("p1", "u1", CouponIssueFrequency.ONCE_PER_MONTH, at))
            .isEqualTo("p1:u1:2026-09");
    }

    @Test
    @DisplayName("ONCE_PER_MONTH 는 같은 월이면 동일하다")
    void oncePerMonth_sameMonth() {
        final Instant first = LocalDateTime.of(2026, 9, 1, 0, 0).atZone(UTC).toInstant();
        final Instant last = LocalDateTime.of(2026, 9, 30, 23, 59).atZone(UTC).toInstant();
        assertThat(CouponWalletDomain.idempotencyKey("p", "u", CouponIssueFrequency.ONCE_PER_MONTH, first))
            .isEqualTo(CouponWalletDomain.idempotencyKey("p", "u", CouponIssueFrequency.ONCE_PER_MONTH, last));
    }

    @Test
    @DisplayName("ONCE_PER_MONTH 는 월이 바뀌면 달라진다")
    void oncePerMonth_adjacentMonths() {
        final Instant sep = LocalDateTime.of(2026, 9, 30, 23, 0).atZone(UTC).toInstant();
        final Instant oct = LocalDateTime.of(2026, 10, 1, 0, 0).atZone(UTC).toInstant();
        assertThat(CouponWalletDomain.idempotencyKey("p", "u", CouponIssueFrequency.ONCE_PER_MONTH, sep))
            .isEqualTo("p:u:2026-09");
        assertThat(CouponWalletDomain.idempotencyKey("p", "u", CouponIssueFrequency.ONCE_PER_MONTH, oct))
            .isEqualTo("p:u:2026-10");
    }

    @Test
    @DisplayName("ONCE_PER_YEAR 는 UTC yyyy 를 붙인다")
    void oncePerYear_format() {
        final Instant at = LocalDateTime.of(2026, 12, 31, 23, 0).atZone(UTC).toInstant();
        assertThat(CouponWalletDomain.idempotencyKey("p1", "u1", CouponIssueFrequency.ONCE_PER_YEAR, at))
            .isEqualTo("p1:u1:2026");
    }
}

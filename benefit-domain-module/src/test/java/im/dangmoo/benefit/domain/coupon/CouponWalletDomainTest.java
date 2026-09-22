package im.dangmoo.benefit.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class CouponWalletDomainTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    @ParameterizedTest
    @CsvSource({
        "policy-1, user-1, policy-1:user-1",
        "abc, xyz, abc:xyz",
        "p, u, p:u"
    })
    @DisplayName("idempotencyKey 는 policyId:userId 형식이다")
    void idempotencyKey_format(final String policyId, final String userId, final String expected) {
        assertThat(CouponWalletDomain.idempotencyKey(policyId, userId)).isEqualTo(expected);
    }

    @Test
    @DisplayName("같은 입력이면 항상 같은 키를 만든다")
    void idempotencyKey_stable() {
        final String first = CouponWalletDomain.idempotencyKey("p", "u");
        final String second = CouponWalletDomain.idempotencyKey("p", "u");
        assertThat(first).isEqualTo(second);
    }

    @Test
    @DisplayName("policyId 또는 userId 가 다르면 키가 다르다")
    void idempotencyKey_distinct() {
        assertThat(CouponWalletDomain.idempotencyKey("p1", "u1"))
            .isNotEqualTo(CouponWalletDomain.idempotencyKey("p2", "u1"));
        assertThat(CouponWalletDomain.idempotencyKey("p1", "u1"))
            .isNotEqualTo(CouponWalletDomain.idempotencyKey("p1", "u2"));
    }

    @Test
    @DisplayName("monthlyIdempotencyKey 는 policyId:userId:yyyy-MM 형식이다")
    void monthlyIdempotencyKey_format() {
        final Instant at = LocalDateTime.of(2026, 9, 23, 12, 0)
            .atZone(SEOUL)
            .toInstant();
        assertThat(CouponWalletDomain.monthlyIdempotencyKey("p1", "u1", at))
            .isEqualTo("p1:u1:2026-09");
    }

    @Test
    @DisplayName("monthlyIdempotencyKey 는 같은 월이면 동일하다")
    void monthlyIdempotencyKey_sameMonth() {
        final Instant first = LocalDateTime.of(2026, 9, 1, 0, 0).atZone(SEOUL).toInstant();
        final Instant last = LocalDateTime.of(2026, 9, 30, 23, 59).atZone(SEOUL).toInstant();
        assertThat(CouponWalletDomain.monthlyIdempotencyKey("p", "u", first))
            .isEqualTo(CouponWalletDomain.monthlyIdempotencyKey("p", "u", last));
    }

    @Test
    @DisplayName("monthlyIdempotencyKey 는 월이 바뀌면 달라진다")
    void monthlyIdempotencyKey_adjacentMonths() {
        final Instant sep = LocalDateTime.of(2026, 9, 30, 23, 0).atZone(SEOUL).toInstant();
        final Instant oct = LocalDateTime.of(2026, 10, 1, 0, 0).atZone(SEOUL).toInstant();
        assertThat(CouponWalletDomain.monthlyIdempotencyKey("p", "u", sep))
            .isEqualTo("p:u:2026-09");
        assertThat(CouponWalletDomain.monthlyIdempotencyKey("p", "u", oct))
            .isEqualTo("p:u:2026-10");
    }
}

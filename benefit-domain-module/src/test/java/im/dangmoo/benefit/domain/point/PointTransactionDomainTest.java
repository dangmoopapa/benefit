package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointIssueFrequency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class PointTransactionDomainTest {

    private static final ZoneId UTC = ZoneOffset.UTC;

    @ParameterizedTest
    @CsvSource({
        "policy-1, user-1, GRANT:policy-1:user-1",
        "p, u, GRANT:p:u"
    })
    @DisplayName("ONCE_PER_USER grantKey 는 GRANT:policyId:userId 형식이다")
    void grantKey_oncePerUser(final String policyId, final String userId, final String expected) {
        assertThat(PointTransactionDomain.grantKey(
            policyId,
            userId,
            PointIssueFrequency.ONCE_PER_USER,
            Instant.parse("2026-09-23T00:00:00Z")
        )).isEqualTo(expected);
    }

    @Test
    @DisplayName("frequency 가 null 이면 ONCE_PER_USER 로 취급한다")
    void grantKey_nullFrequency() {
        assertThat(PointTransactionDomain.grantKey("p", "u", null, Instant.parse("2026-01-01T00:00:00Z")))
            .isEqualTo("GRANT:p:u");
    }

    @Test
    @DisplayName("ONCE_PER_DAY grantKey 는 UTC yyyy-MM-dd 를 붙인다")
    void grantKey_oncePerDay() {
        final Instant at = LocalDateTime.of(2026, 9, 23, 12, 0).atZone(UTC).toInstant();
        assertThat(PointTransactionDomain.grantKey("p1", "u1", PointIssueFrequency.ONCE_PER_DAY, at))
            .isEqualTo("GRANT:p1:u1:2026-09-23");
    }

    @Test
    @DisplayName("ONCE_PER_MONTH grantKey 는 UTC yyyy-MM 를 붙인다")
    void grantKey_oncePerMonth() {
        final Instant at = LocalDateTime.of(2026, 9, 23, 12, 0).atZone(UTC).toInstant();
        assertThat(PointTransactionDomain.grantKey("p1", "u1", PointIssueFrequency.ONCE_PER_MONTH, at))
            .isEqualTo("GRANT:p1:u1:2026-09");
    }

    @Test
    @DisplayName("ONCE_PER_YEAR grantKey 는 UTC yyyy 를 붙인다")
    void grantKey_oncePerYear() {
        final Instant at = LocalDateTime.of(2026, 12, 31, 23, 0).atZone(UTC).toInstant();
        assertThat(PointTransactionDomain.grantKey("p1", "u1", PointIssueFrequency.ONCE_PER_YEAR, at))
            .isEqualTo("GRANT:p1:u1:2026");
    }

    @Test
    @DisplayName("useKey 는 USE:orderId 형식이다")
    void useKey_format() {
        assertThat(PointTransactionDomain.useKey("order-1")).isEqualTo("USE:order-1");
    }
}

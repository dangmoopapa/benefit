package im.dangmoo.benefit.domain.membership;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipContractDomainTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    @ParameterizedTest
    @CsvSource({
        "policy-1, user-1, policy-1:user-1",
        "p, u, p:u"
    })
    @DisplayName("idempotencyKey 는 policyId:userId 형식이다")
    void idempotencyKey_format(final String policyId, final String userId, final String expected) {
        assertThat(MembershipContractDomain.idempotencyKey(policyId, userId)).isEqualTo(expected);
    }

    @Test
    @DisplayName("nextPeriodEnd 는 Asia/Seoul 기준 1개월을 더한다")
    void nextPeriodEnd_plusOneMonth() {
        final Instant from = LocalDateTime.of(2026, 1, 15, 12, 0).atZone(SEOUL).toInstant();
        assertThat(MembershipContractDomain.nextPeriodEnd(from))
            .isEqualTo(ZonedDateTime.ofInstant(from, SEOUL).plusMonths(1).toInstant());
    }

    @Test
    @DisplayName("nextPeriodEnd 는 말일에서도 1개월을 더한다")
    void nextPeriodEnd_endOfMonth() {
        final Instant from = LocalDateTime.of(2026, 1, 31, 0, 0).atZone(SEOUL).toInstant();
        assertThat(MembershipContractDomain.nextPeriodEnd(from))
            .isEqualTo(ZonedDateTime.ofInstant(from, SEOUL).plusMonths(1).toInstant());
    }
}

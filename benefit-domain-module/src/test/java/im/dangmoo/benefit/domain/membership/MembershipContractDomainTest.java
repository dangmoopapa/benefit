package im.dangmoo.benefit.domain.membership;

import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractDocument;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipContractDomainTest {

    private static final ZoneId UTC = ZoneOffset.UTC;

    @ParameterizedTest
    @CsvSource({
        "policy-1, user-1, policy-1:user-1",
        "p, u, p:u"
    })
    @DisplayName("가입 계약키는 policyId:userId 형식이다")
    void joining_contractKey(final String policyId, final String userId, final String expected) {
        assertThat(MembershipContractDomain.joining(policyId, userId, Instant.now()).contractKey())
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("가입 주기 종료일은 가입일의 UTC 기준 1개월 뒤다")
    void joining_periodEnd() {
        final Instant joinedAt = LocalDateTime.of(2026, 1, 15, 12, 0).atZone(UTC).toInstant();
        assertThat(MembershipContractDomain.joining("policy-1", "user-1", joinedAt).periodEnd())
            .isEqualTo(ZonedDateTime.ofInstant(joinedAt, UTC).plusMonths(1).toInstant());
    }

    @Test
    @DisplayName("가입 주기 종료일은 말일에서도 1개월 뒤다")
    void joining_periodEndAtEndOfMonth() {
        final Instant joinedAt = LocalDateTime.of(2026, 1, 31, 0, 0).atZone(UTC).toInstant();
        assertThat(MembershipContractDomain.joining("policy-1", "user-1", joinedAt).periodEnd())
            .isEqualTo(ZonedDateTime.ofInstant(joinedAt, UTC).plusMonths(1).toInstant());
    }

    @Test
    @DisplayName("갱신 주기 종료일은 직전 주기 종료일의 1개월 뒤다")
    void renewing_periodEnd() {
        final Instant periodEnd = LocalDateTime.of(2026, 2, 15, 12, 0).atZone(UTC).toInstant();
        assertThat(MembershipContractDomain.renewing(contract(periodEnd)).periodEnd())
            .isEqualTo(ZonedDateTime.ofInstant(periodEnd, UTC).plusMonths(1).toInstant());
    }

    @Test
    @DisplayName("갱신 계약키는 기존 계약의 policyId:userId 를 따른다")
    void renewing_contractKey() {
        final Instant periodEnd = LocalDateTime.of(2026, 2, 15, 12, 0).atZone(UTC).toInstant();
        assertThat(MembershipContractDomain.renewing(contract(periodEnd)).contractKey())
            .isEqualTo("policy-1:user-1");
    }

    private static MembershipContractDocument contract(final Instant periodEnd) {
        return MembershipContractDocument.join(
            "user-1",
            "policy-1",
            "membership.basic",
            MembershipSeason.SEASON_1,
            periodEnd.minus(Duration.ofDays(30)),
            periodEnd,
            "policy-1:user-1",
            "system"
        );
    }
}

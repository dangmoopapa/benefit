package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PointExpireDomainTest {

    @Test
    @DisplayName("NEVER 는 NEVER_EXPIRES_AT 를 반환한다")
    void resolveExpiresAt_never() {
        final Instant expiresAt = PointExpireDomain.of(
            PointExpireCondition.create(PointExpireType.NEVER, null, null)
        ).resolveExpiresAt(Instant.parse("2026-01-01T00:00:00Z"));
        assertThat(expiresAt).isEqualTo(PointBalance.NEVER_EXPIRES_AT);
    }

    @Test
    @DisplayName("FIXED_AT 은 Asia/Seoul 00시 키로 정규화한다")
    void resolveExpiresAt_fixedAt() {
        final Instant fixed = Instant.parse("2026-03-15T15:30:00Z");
        final Instant expiresAt = PointExpireDomain.of(
            PointExpireCondition.create(PointExpireType.FIXED_AT, fixed, null)
        ).resolveExpiresAt(Instant.parse("2026-01-01T00:00:00Z"));
        assertThat(expiresAt).isEqualTo(PointBalance.toExpiresKey(fixed));
    }

    @Test
    @DisplayName("DAYS_AFTER_GRANT 는 지급일 키 + days 로 계산한다")
    void resolveExpiresAt_daysAfterGrant() {
        final Instant grantedAt = Instant.parse("2026-01-01T15:30:00Z");
        final Instant expiresAt = PointExpireDomain.of(
            PointExpireCondition.create(PointExpireType.DAYS_AFTER_GRANT, null, 10)
        ).resolveExpiresAt(grantedAt);
        assertThat(expiresAt).isEqualTo(Instant.parse("2026-01-11T15:00:00Z"));
    }

    @Test
    @DisplayName("NEVER 만료는 클라이언트에 null 로 내려준다")
    void never_clientNull() {
        assertThat(PointExpireDomain.isNever(PointBalance.NEVER_EXPIRES_AT)).isTrue();
        assertThat(PointExpireDomain.toClientExpiresAt(PointBalance.NEVER_EXPIRES_AT)).isNull();
    }

    @Test
    @DisplayName("일반 만료는 그대로 내려준다")
    void normal_clientPassthrough() {
        final Instant expiresAt = Instant.parse("2026-02-01T15:00:00Z");
        assertThat(PointExpireDomain.isNever(expiresAt)).isFalse();
        assertThat(PointExpireDomain.toClientExpiresAt(expiresAt)).isEqualTo(expiresAt);
    }
}

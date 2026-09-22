package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireType;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointIssueCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PointDomainTest {

    @Test
    @DisplayName("DAYS_AFTER_GRANT 만료일을 Asia/Seoul 00시로 계산한다")
    void expire_daysAfterGrant() {
        final Instant grantedAt = Instant.parse("2026-01-01T15:30:00Z");
        final Instant expiresAt = PointExpireDomain.of(
            PointExpireCondition.create(PointExpireType.DAYS_AFTER_GRANT, null, 10)
        ).resolveExpiresAt(grantedAt);
        assertThat(expiresAt).isEqualTo(Instant.parse("2026-01-11T15:00:00Z"));
    }

    @Test
    @DisplayName("NEVER 만료는 클라이언트에 null 로 내려준다")
    void expire_neverClientNull() {
        assertThat(PointExpireDomain.isNever(PointBalance.NEVER_EXPIRES_AT)).isTrue();
        assertThat(PointExpireDomain.toClientExpiresAt(PointBalance.NEVER_EXPIRES_AT)).isNull();
    }

    @Test
    @DisplayName("만료된 버킷은 가용 포인트에서 제외한다")
    void balance_excludesExpired() {
        final Instant now = Instant.parse("2026-01-10T00:00:00Z");
        final PointBalance balance = PointBalance.create("u1");
        balance.increase(Instant.parse("2026-01-01T00:00:00Z"), 100L);
        balance.increase(Instant.parse("2026-02-01T00:00:00Z"), 50L);
        balance.increase(PointBalance.NEVER_EXPIRES_AT, 30L);

        final PointBalanceDomain domain = PointBalanceDomain.of(balance);
        assertThat(domain.totalAmount()).isEqualTo(180L);
        assertThat(domain.availableAmount(now)).isEqualTo(80L);
        assertThat(domain.expiringAmount(now)).isEqualTo(100L);
    }

    @Test
    @DisplayName("지급 기간 밖이면 조건을 만족하지 않는다")
    void issue_outsideWindow() {
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-31T00:00:00Z"),
                2L,
                List.of(),
                List.of()
            )
        );
        assertThat(domain.isSatisfiedAt(Instant.parse("2026-01-15T00:00:00Z"))).isTrue();
        assertThat(domain.isSatisfiedAt(Instant.parse("2026-02-01T00:00:00Z"))).isFalse();
    }

    @Test
    @DisplayName("사용 시 만료가 가까운 버킷부터 차감한다")
    void use_fifoByExpiresAt() {
        final Instant now = Instant.parse("2026-01-01T00:00:00Z");
        final Instant near = PointBalance.toExpiresKey(Instant.parse("2026-01-05T00:00:00Z"));
        final Instant far = PointBalance.toExpiresKey(Instant.parse("2026-01-20T00:00:00Z"));
        final PointBalance balance = PointBalance.create("u1");
        balance.increase(near, 30L);
        balance.increase(far, 50L);

        balance.decreaseByExpiresAt(40, now);

        assertThat(balance.getAmountsByExpiresAt().get(near)).isNull();
        assertThat(balance.getAmountsByExpiresAt().get(far)).isEqualTo(40L);
    }
}

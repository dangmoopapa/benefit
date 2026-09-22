package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PointBalanceDomainTest {

    @Test
    @DisplayName("만료된 버킷은 가용에서 제외하고 expiring 에 넣는다")
    void excludesExpired() {
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
    @DisplayName("빈 잔액은 전부 0 이다")
    void empty() {
        final Instant now = Instant.parse("2026-01-01T00:00:00Z");
        final PointBalanceDomain domain = PointBalanceDomain.of(PointBalance.create("u1"));
        assertThat(domain.totalAmount()).isZero();
        assertThat(domain.availableAmount(now)).isZero();
        assertThat(domain.expiringAmount(now)).isZero();
        assertThat(domain.availableAmountsByExpiresAt(now)).isEmpty();
    }

    @Test
    @DisplayName("availableAmountsByExpiresAt 은 가용·양수 버킷만 반환한다")
    void availableAmountsByExpiresAt() {
        final Instant now = Instant.parse("2026-01-10T00:00:00Z");
        final Instant past = Instant.parse("2026-01-01T00:00:00Z");
        final Instant future = Instant.parse("2026-02-01T00:00:00Z");
        final PointBalance balance = PointBalance.create("u1");
        balance.increase(past, 100L);
        balance.increase(future, 50L);
        balance.increase(PointBalance.NEVER_EXPIRES_AT, 30L);

        assertThat(PointBalanceDomain.of(balance).availableAmountsByExpiresAt(now))
            .containsEntry(PointBalance.toExpiresKey(future), 50L)
            .containsEntry(PointBalance.NEVER_EXPIRES_AT, 30L)
            .doesNotContainKey(PointBalance.toExpiresKey(past));
    }
}

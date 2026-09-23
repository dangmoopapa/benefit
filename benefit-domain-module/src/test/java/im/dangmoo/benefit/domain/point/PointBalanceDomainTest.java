package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.data.entity.point.balance.PointBalanceDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PointBalanceDomainTest {

    private static final Instant NOW = Instant.parse("2026-01-10T00:00:00Z");
    private static final Instant PAST = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant FUTURE = Instant.parse("2026-02-01T00:00:00Z");

    @Test
    @DisplayName("총 잔액은 만료 여부와 무관하게 모두 더한다")
    void totalAmount() {
        assertThat(PointBalanceDomain.of(balance()).totalAmount()).isEqualTo(180L);
    }

    @Test
    @DisplayName("가용 잔액은 만료된 버킷을 제외한다")
    void availableAmountAt() {
        assertThat(PointBalanceDomain.of(balance()).availableAmountAt(NOW)).isEqualTo(80L);
    }

    @Test
    @DisplayName("가용 버킷은 만료되지 않고 금액이 남은 것만 반환한다")
    void availableAmountsByExpiresAt() {
        assertThat(PointBalanceDomain.of(balance()).availableAmountsByExpiresAt(NOW))
            .containsEntry(PointBalanceDocument.toExpiresKey(FUTURE), 50L)
            .containsEntry(PointBalanceDocument.NEVER_EXPIRES_AT, 30L)
            .doesNotContainKey(PointBalanceDocument.toExpiresKey(PAST));
    }

    @Test
    @DisplayName("만료 잔액은 기준 시각을 지난 버킷만 더한다")
    void expiredAmountAt() {
        assertThat(PointBalanceDomain.of(balance()).expiredAmountAt(NOW)).isEqualTo(100L);
    }

    @Test
    @DisplayName("만료 버킷은 기준 시각을 지난 것만 반환한다")
    void expiredAmountsByExpiresAt() {
        assertThat(PointBalanceDomain.of(balance()).expiredAmountsByExpiresAt(NOW))
            .containsExactly(Map.entry(PointBalanceDocument.toExpiresKey(PAST), 100L));
    }

    @Test
    @DisplayName("빈 잔액은 전부 0 이다")
    void empty() {
        final PointBalanceDomain pointBalance = PointBalanceDomain.of(PointBalanceDocument.create("u1"));
        assertThat(pointBalance.totalAmount()).isZero();
        assertThat(pointBalance.availableAmountAt(NOW)).isZero();
        assertThat(pointBalance.availableAmountsByExpiresAt(NOW)).isEmpty();
        assertThat(pointBalance.expiredAmountAt(NOW)).isZero();
        assertThat(pointBalance.expiredAmountsByExpiresAt(NOW)).isEmpty();
    }

    private static PointBalanceDocument balance() {
        final PointBalanceDocument balance = PointBalanceDocument.create("u1");
        balance.increase(PAST, 100L);
        balance.increase(FUTURE, 50L);
        balance.increase(PointBalanceDocument.NEVER_EXPIRES_AT, 30L);
        return balance;
    }
}

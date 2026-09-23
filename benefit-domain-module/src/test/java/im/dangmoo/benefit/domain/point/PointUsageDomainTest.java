package im.dangmoo.benefit.domain.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointUsageDomainTest {

    @Test
    @DisplayName("잔액 안의 금액이면 USABLE")
    void usabilityOf_usable() {
        assertThat(PointUsageDomain.of(100L).usabilityOf(100L)).isEqualTo(PointUsageDomain.Usability.USABLE);
        assertThat(PointUsageDomain.of(100L).usabilityOf(1L)).isEqualTo(PointUsageDomain.Usability.USABLE);
    }

    @Test
    @DisplayName("사용 금액이 0 이하면 INVALID_AMOUNT")
    void usabilityOf_invalidAmount() {
        assertThat(PointUsageDomain.of(100L).usabilityOf(0L))
            .isEqualTo(PointUsageDomain.Usability.INVALID_AMOUNT);
        assertThat(PointUsageDomain.of(100L).usabilityOf(-1L))
            .isEqualTo(PointUsageDomain.Usability.INVALID_AMOUNT);
    }

    @Test
    @DisplayName("사용 금액이 잔액보다 크면 INSUFFICIENT_BALANCE")
    void usabilityOf_insufficientBalance() {
        assertThat(PointUsageDomain.of(100L).usabilityOf(101L))
            .isEqualTo(PointUsageDomain.Usability.INSUFFICIENT_BALANCE);
        assertThat(PointUsageDomain.of(0L).usabilityOf(1L))
            .isEqualTo(PointUsageDomain.Usability.INSUFFICIENT_BALANCE);
    }
}

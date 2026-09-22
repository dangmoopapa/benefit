package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponIssueDomainTest {

    private static final ZoneId UTC = ZoneOffset.UTC;

    @Test
    @DisplayName("기간·요일·시간 제한이 없으면 항상 통과한다")
    void isSatisfiedAt_unrestricted() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfiedAt(Instant.parse("2024-06-15T00:00:00Z"))).isTrue();
    }

    @Test
    @DisplayName("startAt 이전이면 실패한다")
    void isSatisfiedAt_beforeStartAt() {
        final Instant start = LocalDateTime.of(2024, 6, 10, 0, 0).atZone(UTC).toInstant();
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(start, null, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 9, 23, 0).atZone(UTC).toInstant())).isFalse();
    }

    @Test
    @DisplayName("startAt 과 같으면 통과한다")
    void isSatisfiedAt_atStartAt() {
        final Instant start = LocalDateTime.of(2024, 6, 10, 0, 0).atZone(UTC).toInstant();
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(start, null, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfiedAt(start)).isTrue();
    }

    @Test
    @DisplayName("endAt 이후면 실패한다")
    void isSatisfiedAt_afterEndAt() {
        final Instant end = LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant();
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, end, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfiedAt(end.plusSeconds(1))).isFalse();
    }

    @Test
    @DisplayName("endAt 과 같으면 통과한다")
    void isSatisfiedAt_atEndAt() {
        final Instant end = LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant();
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, end, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfiedAt(end)).isTrue();
    }

    @Test
    @DisplayName("허용 요일이면 통과한다")
    void isSatisfiedAt_allowedDay() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, null, List.of(DayOfWeek.MONDAY), List.of())
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isTrue();
    }

    @Test
    @DisplayName("허용 요일이 아니면 실패한다")
    void isSatisfiedAt_disallowedDay() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, null, List.of(DayOfWeek.TUESDAY), List.of())
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isFalse();
    }

    @Test
    @DisplayName("허용 시간이면 통과한다")
    void isSatisfiedAt_allowedHour() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, null, List.of(), List.of(12))
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isTrue();
    }

    @Test
    @DisplayName("허용 시간이 아니면 실패한다")
    void isSatisfiedAt_disallowedHour() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, null, List.of(), List.of(10, 11))
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isFalse();
    }

    @Test
    @DisplayName("요일은 맞지만 시간이 틀리면 실패한다")
    void isSatisfiedAt_dayOkHourFail() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, null, List.of(DayOfWeek.MONDAY), List.of(10))
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isFalse();
    }

    @Test
    @DisplayName("기간·요일·시간을 모두 만족하면 통과한다")
    void isSatisfiedAt_allConstraints() {
        final Instant start = LocalDateTime.of(2024, 6, 10, 0, 0).atZone(UTC).toInstant();
        final Instant end = LocalDateTime.of(2024, 6, 10, 23, 0).atZone(UTC).toInstant();
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(start, end, null, List.of(DayOfWeek.MONDAY), List.of(12))
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isTrue();
    }

    @Test
    @DisplayName("재고 제한이 없으면 소진되지 않는다")
    void isSatisfied_unlimitedStock() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfied(Instant.parse("2024-06-15T00:00:00Z"), 999_999L)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
        "10, 0, true",
        "10, 9, true",
        "10, 10, false",
        "10, 11, false",
        "0, 0, false"
    })
    @DisplayName("issuedCount 가 stockQuantity 이상이면 실패한다")
    void isSatisfied_stockBoundaries(final long stock, final long issued, final boolean expected) {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, stock, List.of(), List.of())
        );
        assertThat(domain.isSatisfied(Instant.parse("2024-06-15T00:00:00Z"), issued)).isEqualTo(expected);
    }

    @Test
    @DisplayName("시간이 맞지 않으면 재고가 남아도 실패한다")
    void isSatisfied_timeFail() {
        final Instant start = LocalDateTime.of(2024, 6, 10, 0, 0).atZone(UTC).toInstant();
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(start, null, 100L, List.of(), List.of())
        );
        assertThat(domain.isSatisfied(LocalDateTime.of(2024, 6, 9, 0, 0).atZone(UTC).toInstant(), 0L)).isFalse();
    }

    @Test
    @DisplayName("재고가 소진되면 시간이 맞아도 실패한다")
    void isSatisfied_stockExhausted() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, 1L, List.of(), List.of())
        );
        assertThat(domain.isSatisfied(Instant.parse("2024-06-15T00:00:00Z"), 1L)).isFalse();
    }

    @Test
    @DisplayName("시간과 재고를 모두 만족하면 통과한다")
    void isSatisfied_bothOk() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, 10L, List.of(), List.of())
        );
        assertThat(domain.isSatisfied(Instant.parse("2024-06-15T00:00:00Z"), 5L)).isTrue();
    }

    @Test
    @DisplayName("getStockQuantity 는 설정값을 반환한다")
    void getStockQuantity_configured() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, 7L, List.of(), List.of())
        );
        assertThat(domain.getStockQuantity()).isEqualTo(7L);
    }

    @Test
    @DisplayName("getStockQuantity 는 무제한이면 null 이다")
    void getStockQuantity_unlimited() {
        final CouponIssueDomain domain = CouponIssueDomain.of(
            CouponIssueCondition.create(null, null, null, List.of(), List.of())
        );
        assertThat(domain.getStockQuantity()).isNull();
    }
}

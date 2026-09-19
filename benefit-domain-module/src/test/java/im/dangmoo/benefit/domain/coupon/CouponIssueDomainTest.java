package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponIssueDomainTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private static Instant seoul(final int year, final int month, final int day, final int hour) {
        return LocalDateTime.of(year, month, day, hour, 0).atZone(SEOUL).toInstant();
    }

    private static CouponIssueDomain domain(
        final Instant startAt,
        final Instant endAt,
        final Long stockQuantity,
        final List<DayOfWeek> days,
        final List<Integer> hours
    ) {
        return CouponIssueDomain.of(CouponIssueCondition.create(startAt, endAt, stockQuantity, days, hours));
    }

    @Nested
    @DisplayName("isSatisfiedAt")
    class IsSatisfiedAt {

        @Test
        @DisplayName("기간·요일·시간 제한이 없으면 항상 통과한다")
        void unrestricted_alwaysTrue() {
            final CouponIssueDomain domain = domain(null, null, null, List.of(), List.of());
            assertThat(domain.isSatisfiedAt(Instant.parse("2024-06-15T00:00:00Z"))).isTrue();
        }

        @Test
        @DisplayName("startAt 이전이면 실패한다")
        void beforeStartAt_false() {
            final Instant start = seoul(2024, 6, 10, 0);
            final CouponIssueDomain domain = domain(start, null, null, List.of(), List.of());
            assertThat(domain.isSatisfiedAt(seoul(2024, 6, 9, 23))).isFalse();
        }

        @Test
        @DisplayName("startAt 과 같으면 통과한다")
        void atStartAt_true() {
            final Instant start = seoul(2024, 6, 10, 0);
            final CouponIssueDomain domain = domain(start, null, null, List.of(), List.of());
            assertThat(domain.isSatisfiedAt(start)).isTrue();
        }

        @Test
        @DisplayName("endAt 이후면 실패한다")
        void afterEndAt_false() {
            final Instant end = seoul(2024, 6, 10, 12);
            final CouponIssueDomain domain = domain(null, end, null, List.of(), List.of());
            assertThat(domain.isSatisfiedAt(seoul(2024, 6, 10, 12).plusSeconds(1))).isFalse();
        }

        @Test
        @DisplayName("endAt 과 같으면 통과한다")
        void atEndAt_true() {
            final Instant end = seoul(2024, 6, 10, 12);
            final CouponIssueDomain domain = domain(null, end, null, List.of(), List.of());
            assertThat(domain.isSatisfiedAt(end)).isTrue();
        }

        @Test
        @DisplayName("허용 요일에 포함되면 통과한다")
        void allowedDay_true() {
            // 2024-06-10 = Monday (KST)
            final CouponIssueDomain domain = domain(null, null, null, List.of(DayOfWeek.MONDAY), List.of());
            assertThat(domain.isSatisfiedAt(seoul(2024, 6, 10, 10))).isTrue();
        }

        @Test
        @DisplayName("허용 요일에 없으면 실패한다")
        void disallowedDay_false() {
            final CouponIssueDomain domain = domain(null, null, null, List.of(DayOfWeek.MONDAY), List.of());
            assertThat(domain.isSatisfiedAt(seoul(2024, 6, 11, 10))).isFalse();
        }

        @Test
        @DisplayName("허용 시간에 포함되면 통과한다")
        void allowedHour_true() {
            final CouponIssueDomain domain = domain(null, null, null, List.of(), List.of(14));
            assertThat(domain.isSatisfiedAt(seoul(2024, 6, 10, 14))).isTrue();
        }

        @Test
        @DisplayName("허용 시간에 없으면 실패한다")
        void disallowedHour_false() {
            final CouponIssueDomain domain = domain(null, null, null, List.of(), List.of(14));
            assertThat(domain.isSatisfiedAt(seoul(2024, 6, 10, 15))).isFalse();
        }

        @Test
        @DisplayName("요일은 통과해도 시간이 틀리면 실패한다")
        void dayOkButHourFail_false() {
            final CouponIssueDomain domain = domain(
                null,
                null,
                null,
                List.of(DayOfWeek.MONDAY),
                List.of(10)
            );
            assertThat(domain.isSatisfiedAt(seoul(2024, 6, 10, 11))).isFalse();
        }

        @Test
        @DisplayName("기간·요일·시간을 모두 만족하면 통과한다")
        void allConstraintsSatisfied_true() {
            final CouponIssueDomain domain = domain(
                seoul(2024, 6, 1, 0),
                seoul(2024, 6, 30, 23),
                null,
                List.of(DayOfWeek.MONDAY),
                List.of(10, 11)
            );
            assertThat(domain.isSatisfiedAt(seoul(2024, 6, 10, 10))).isTrue();
        }
    }

    @Nested
    @DisplayName("isStockExhausted")
    class IsStockExhausted {

        @Test
        @DisplayName("재고 제한이 없으면 소진되지 않는다")
        void unlimited_neverExhausted() {
            final CouponIssueDomain domain = domain(null, null, null, List.of(), List.of());
            assertThat(domain.isStockExhausted(0)).isFalse();
            assertThat(domain.isStockExhausted(Long.MAX_VALUE)).isFalse();
        }

        @ParameterizedTest
        @CsvSource({
            "100, 0, false",
            "100, 99, false",
            "100, 100, true",
            "100, 101, true",
            "1, 0, false",
            "1, 1, true"
        })
        @DisplayName("issuedCount 와 stockQuantity 경계값을 검증한다")
        void boundaries(final long stock, final long issued, final boolean exhausted) {
            final CouponIssueDomain domain = domain(null, null, stock, List.of(), List.of());
            assertThat(domain.isStockExhausted(issued)).isEqualTo(exhausted);
        }
    }

    @Nested
    @DisplayName("isSatisfied")
    class IsSatisfied {

        @Test
        @DisplayName("시간 조건 실패면 재고가 남아도 실패한다")
        void timeFail_false() {
            final CouponIssueDomain domain = domain(
                seoul(2024, 6, 10, 0),
                null,
                100L,
                List.of(),
                List.of()
            );
            assertThat(domain.isSatisfied(seoul(2024, 6, 9, 0), 0)).isFalse();
        }

        @Test
        @DisplayName("시간은 통과해도 재고 소진이면 실패한다")
        void stockExhausted_false() {
            final CouponIssueDomain domain = domain(null, null, 10L, List.of(), List.of());
            assertThat(domain.isSatisfied(Instant.parse("2024-06-15T00:00:00Z"), 10)).isFalse();
        }

        @Test
        @DisplayName("시간과 재고를 모두 만족하면 통과한다")
        void bothOk_true() {
            final CouponIssueDomain domain = domain(null, null, 10L, List.of(), List.of());
            assertThat(domain.isSatisfied(Instant.parse("2024-06-15T00:00:00Z"), 9)).isTrue();
        }
    }

    @Nested
    @DisplayName("getStockQuantity")
    class GetStockQuantity {

        @ParameterizedTest
        @ValueSource(longs = {1, 100, 30000})
        void returnsConfiguredStock(final long stock) {
            assertThat(domain(null, null, stock, List.of(), List.of()).getStockQuantity()).isEqualTo(stock);
        }

        @Test
        void returnsNullWhenUnlimited() {
            assertThat(domain(null, null, null, List.of(), List.of()).getStockQuantity()).isNull();
        }
    }
}

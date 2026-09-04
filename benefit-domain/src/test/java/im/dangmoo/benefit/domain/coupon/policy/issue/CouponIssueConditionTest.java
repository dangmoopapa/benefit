package im.dangmoo.benefit.domain.coupon.policy.issue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponIssueConditionTest {

    private static final Instant PERIOD_START = Instant.parse("2026-03-01T00:00:00Z");
    private static final Instant PERIOD_END = Instant.parse("2026-03-31T23:59:59Z");

    @Test
    @DisplayName("발급 기간 안이면 통과한다")
    void passesWithinIssuePeriod() {
        final CouponIssueCondition condition = CouponIssueCondition.create(
            CouponIssuablePeriod.create(PERIOD_START, PERIOD_END),
            List.of(),
            List.of(),
            null,
            null
        );

        assertThat(condition.isSatisfiedAt(Instant.parse("2026-03-15T12:00:00Z"))).isTrue();
    }

    @Test
    @DisplayName("발급 기간 이전이면 실패한다")
    void failsBeforeIssuePeriod() {
        final CouponIssueCondition condition = CouponIssueCondition.create(
            CouponIssuablePeriod.create(PERIOD_START, PERIOD_END),
            List.of(),
            List.of(),
            null,
            null
        );

        assertThat(condition.isSatisfiedAt(Instant.parse("2026-02-28T23:59:59Z"))).isFalse();
    }

    @Test
    @DisplayName("발급 기간 이후면 실패한다")
    void failsAfterIssuePeriod() {
        final CouponIssueCondition condition = CouponIssueCondition.create(
            CouponIssuablePeriod.create(PERIOD_START, PERIOD_END),
            List.of(),
            List.of(),
            null,
            null
        );

        assertThat(condition.isSatisfiedAt(Instant.parse("2026-04-01T00:00:00Z"))).isFalse();
    }

    @Test
    @DisplayName("허용되지 않은 요일이면 실패한다")
    void failsOnDisallowedWeekday() {
        final CouponIssueCondition condition = CouponIssueCondition.create(
            CouponIssuablePeriod.create(PERIOD_START, PERIOD_END),
            List.of(CouponIssuableWeekday.MON),
            List.of(),
            null,
            null
        );

        assertThat(condition.isSatisfiedAt(Instant.parse("2026-03-15T12:00:00Z"))).isFalse();
    }

    @Test
    @DisplayName("허용된 요일이면 통과한다")
    void passesOnAllowedWeekday() {
        final CouponIssueCondition condition = CouponIssueCondition.create(
            CouponIssuablePeriod.create(PERIOD_START, PERIOD_END),
            List.of(CouponIssuableWeekday.SUN),
            List.of(),
            null,
            null
        );

        assertThat(condition.isSatisfiedAt(Instant.parse("2026-03-15T12:00:00Z"))).isTrue();
    }

    @Test
    @DisplayName("허용 시간대가 아니면 실패한다")
    void failsOutsideTimeRange() {
        final CouponIssueCondition condition = CouponIssueCondition.create(
            CouponIssuablePeriod.create(PERIOD_START, PERIOD_END),
            List.of(),
            List.of(CouponIssuableTime.create(LocalTime.of(10, 0), LocalTime.of(12, 0))),
            null,
            null
        );

        assertThat(condition.isSatisfiedAt(Instant.parse("2026-03-15T13:00:00Z"))).isFalse();
    }

    @Test
    @DisplayName("허용 시간대면 통과한다")
    void passesInsideTimeRange() {
        final CouponIssueCondition condition = CouponIssueCondition.create(
            CouponIssuablePeriod.create(PERIOD_START, PERIOD_END),
            List.of(),
            List.of(CouponIssuableTime.create(LocalTime.of(10, 0), LocalTime.of(14, 0))),
            null,
            null
        );

        assertThat(condition.isSatisfiedAt(Instant.parse("2026-03-15T12:00:00Z"))).isTrue();
    }

    @Test
    @DisplayName("총 수량 제한이 없으면 통과한다")
    void passesWhenQuantityUnlimited() {
        final CouponIssueCondition condition = CouponIssueCondition.create(
            CouponIssuablePeriod.create(PERIOD_START, PERIOD_END),
            List.of(),
            List.of(),
            null,
            null
        );

        assertThat(condition.hasRemainingQuantity(1_000_000L)).isTrue();
    }

    @Test
    @DisplayName("총 수량 미만이면 통과한다")
    void passesWhenBelowQuantityLimit() {
        final CouponIssueCondition condition = CouponIssueCondition.create(
            CouponIssuablePeriod.create(PERIOD_START, PERIOD_END),
            List.of(),
            List.of(),
            null,
            100L
        );

        assertThat(condition.hasRemainingQuantity(99L)).isTrue();
    }

    @Test
    @DisplayName("총 수량에 도달하면 실패한다")
    void failsWhenQuantityExhausted() {
        final CouponIssueCondition condition = CouponIssueCondition.create(
            CouponIssuablePeriod.create(PERIOD_START, PERIOD_END),
            List.of(),
            List.of(),
            null,
            100L
        );

        assertThat(condition.hasRemainingQuantity(100L)).isFalse();
    }
}

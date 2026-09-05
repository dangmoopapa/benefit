package im.dangmoo.benefit.domain.coupon.policy.usage;

import im.dangmoo.benefit.domain.coupon.document.policy.usage.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponUsageConditionTest {

    private static final Instant ISSUED_AT = Instant.parse("2026-03-15T10:30:00Z");

    @Test
    @DisplayName("즉시 사용 불가인데 발급 시각이면 실패한다")
    void failsWhenNotUsableImmediatelyAtIssueInstant() {
        final CouponUsageCondition condition = CouponUsageCondition.create(
            CouponUsageValidity.create(CouponUsageValidityType.AFTER_PURCHASE, null, null, null, null),
            false,
            List.of(),
            List.of(),
            CouponOrderCondition.create(null, null, List.of(), List.of(), List.of(), false, null),
            CouponStackingCondition.create(true, true, true, true, true, 0, false),
            CouponUsageLimit.create(null, null, null, null, null, null)
        );

        assertThat(condition.isSatisfiedAt(ISSUED_AT, ISSUED_AT)).isFalse();
    }

    @Test
    @DisplayName("즉시 사용 불가여도 발급 이후면 통과한다")
    void passesWhenNotUsableImmediatelyAfterIssue() {
        final CouponUsageCondition condition = CouponUsageCondition.create(
            CouponUsageValidity.create(CouponUsageValidityType.AFTER_PURCHASE, null, null, null, null),
            false,
            List.of(),
            List.of(),
            CouponOrderCondition.create(null, null, List.of(), List.of(), List.of(), false, null),
            CouponStackingCondition.create(true, true, true, true, true, 0, false),
            CouponUsageLimit.create(null, null, null, null, null, null)
        );

        assertThat(condition.isSatisfiedAt(ISSUED_AT, ISSUED_AT.plusSeconds(1))).isTrue();
    }

    @Test
    @DisplayName("허용되지 않은 요일이면 실패한다")
    void failsOnDisallowedWeekday() {
        final CouponUsageCondition condition = CouponUsageCondition.create(
            CouponUsageValidity.create(CouponUsageValidityType.AFTER_PURCHASE, null, null, null, null),
            true,
            List.of(CouponUsableWeekday.MON),
            List.of(),
            CouponOrderCondition.create(null, null, List.of(), List.of(), List.of(), false, null),
            CouponStackingCondition.create(true, true, true, true, true, 0, false),
            CouponUsageLimit.create(null, null, null, null, null, null)
        );

        assertThat(condition.isSatisfiedAt(ISSUED_AT, Instant.parse("2026-03-15T12:00:00Z"))).isFalse();
    }

    @Test
    @DisplayName("허용 시간대가 아니면 실패한다")
    void failsOutsideTimeRange() {
        final CouponUsageCondition condition = CouponUsageCondition.create(
            CouponUsageValidity.create(CouponUsageValidityType.AFTER_PURCHASE, null, null, null, null),
            true,
            List.of(),
            List.of(CouponUsableTime.create(LocalTime.of(10, 0), LocalTime.of(11, 0))),
            CouponOrderCondition.create(null, null, List.of(), List.of(), List.of(), false, null),
            CouponStackingCondition.create(true, true, true, true, true, 0, false),
            CouponUsageLimit.create(null, null, null, null, null, null)
        );

        assertThat(condition.isSatisfiedAt(ISSUED_AT, Instant.parse("2026-03-15T12:00:00Z"))).isFalse();
    }

    @Test
    @DisplayName("유효기간·요일·시간이 맞으면 통과한다")
    void passesWhenWindowWeekdayAndTimeMatch() {
        final CouponUsageCondition condition = CouponUsageCondition.create(
            CouponUsageValidity.create(CouponUsageValidityType.AFTER_PURCHASE, null, null, null, null),
            true,
            List.of(CouponUsableWeekday.SUN),
            List.of(CouponUsableTime.create(LocalTime.of(10, 0), LocalTime.of(14, 0))),
            CouponOrderCondition.create(null, null, List.of(), List.of(), List.of(), false, null),
            CouponStackingCondition.create(true, true, true, true, true, 0, false),
            CouponUsageLimit.create(null, null, null, null, null, null)
        );

        assertThat(condition.isSatisfiedAt(ISSUED_AT, Instant.parse("2026-03-15T12:00:00Z"))).isTrue();
    }
}

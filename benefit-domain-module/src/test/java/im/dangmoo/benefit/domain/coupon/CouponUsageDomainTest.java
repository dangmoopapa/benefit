package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponApplyCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageValidityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponUsageDomainTest {

    @Test
    @DisplayName("validityType 이 null 이면 만료일은 null 이다")
    void resolveExpiresAt_nullValidity() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, null, null),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.resolveExpiresAt(Instant.parse("2026-01-01T00:00:00Z"))).isNull();
    }

    @Test
    @DisplayName("FIXED_PERIOD 이면 endAt 을 만료일로 쓴다")
    void resolveExpiresAt_fixedPeriod() {
        final Instant endAt = Instant.parse("2026-02-01T00:00:00Z");
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(CouponUsageValidityType.FIXED_PERIOD, null, endAt, null, null, null),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.resolveExpiresAt(Instant.parse("2026-01-01T00:00:00Z"))).isEqualTo(endAt);
    }

    @Test
    @DisplayName("UNTIL_DAYS_AFTER_ISSUE 는 발급일 + days 이다")
    void resolveExpiresAt_untilDays() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(
                CouponUsageValidityType.UNTIL_DAYS_AFTER_ISSUE, null, null, 10, null, null
            ),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.resolveExpiresAt(Instant.parse("2026-01-01T00:00:00Z")))
            .isEqualTo(Instant.parse("2026-01-11T00:00:00Z"));
    }

    @Test
    @DisplayName("FOR_DAYS_AFTER_ISSUE 는 발급일 + days 이다")
    void resolveExpiresAt_forDays() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(
                CouponUsageValidityType.FOR_DAYS_AFTER_ISSUE, null, null, 5, null, null
            ),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.resolveExpiresAt(Instant.parse("2026-01-01T00:00:00Z")))
            .isEqualTo(Instant.parse("2026-01-06T00:00:00Z"));
    }

    @Test
    @DisplayName("daysAfterIssue 가 null 이면 만료일은 null 이다")
    void resolveExpiresAt_daysNull() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(
                CouponUsageValidityType.UNTIL_DAYS_AFTER_ISSUE, null, null, null, null, null
            ),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.resolveExpiresAt(Instant.parse("2026-01-01T00:00:00Z"))).isNull();
    }

    @Test
    @DisplayName("만료 이후면 실패한다")
    void isSatisfied_afterExpiresAt() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, null, null),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        final Instant expiresAt = Instant.parse("2026-01-10T00:00:00Z");
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            expiresAt,
            expiresAt.plusSeconds(1),
            0L,
            null,
            null,
            null,
            null,
            null
        )).isFalse();
    }

    @Test
    @DisplayName("만료 시점과 같으면 통과한다")
    void isSatisfied_atExpiresAt() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, null, null),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        final Instant expiresAt = Instant.parse("2026-01-10T00:00:00Z");
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            expiresAt,
            expiresAt,
            0L,
            null,
            null,
            null,
            null,
            null
        )).isTrue();
    }

    @Test
    @DisplayName("FIXED_PERIOD 시작 전이면 실패한다")
    void isSatisfied_fixedPeriodBeforeStart() {
        final Instant start = Instant.parse("2026-01-10T00:00:00Z");
        final Instant end = Instant.parse("2026-01-20T00:00:00Z");
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(CouponUsageValidityType.FIXED_PERIOD, start, end, null, null, null),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            end,
            start.minusSeconds(1),
            0L,
            null,
            null,
            null,
            null,
            null
        )).isFalse();
    }

    @Test
    @DisplayName("FIXED_PERIOD 종료 이후면 실패한다")
    void isSatisfied_fixedPeriodAfterEnd() {
        final Instant start = Instant.parse("2026-01-10T00:00:00Z");
        final Instant end = Instant.parse("2026-01-20T00:00:00Z");
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(CouponUsageValidityType.FIXED_PERIOD, start, end, null, null, null),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            end,
            end.plusSeconds(1),
            0L,
            null,
            null,
            null,
            null,
            null
        )).isFalse();
    }

    @Test
    @DisplayName("FIXED_PERIOD 기간 안이면 통과한다")
    void isSatisfied_fixedPeriodInRange() {
        final Instant start = Instant.parse("2026-01-10T00:00:00Z");
        final Instant end = Instant.parse("2026-01-20T00:00:00Z");
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(CouponUsageValidityType.FIXED_PERIOD, start, end, null, null, null),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            end,
            Instant.parse("2026-01-15T00:00:00Z"),
            0L,
            null,
            null,
            null,
            null,
            null
        )).isTrue();
    }

    @Test
    @DisplayName("발급 후 일수가 지나면 실패한다")
    void isSatisfied_daysAfterIssueExpired() {
        final Instant issuedAt = Instant.parse("2026-01-01T00:00:00Z");
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(
                CouponUsageValidityType.UNTIL_DAYS_AFTER_ISSUE, null, null, 10, null, null
            ),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            issuedAt,
            null,
            Instant.parse("2026-01-12T00:00:00Z"),
            0L,
            null,
            null,
            null,
            null,
            null
        )).isFalse();
    }

    @Test
    @DisplayName("발급 후 일수 안이면 통과한다")
    void isSatisfied_daysAfterIssueValid() {
        final Instant issuedAt = Instant.parse("2026-01-01T00:00:00Z");
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(
                CouponUsageValidityType.FOR_DAYS_AFTER_ISSUE, null, null, 10, null, null
            ),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            issuedAt,
            null,
            Instant.parse("2026-01-05T00:00:00Z"),
            0L,
            null,
            null,
            null,
            null,
            null
        )).isTrue();
    }

    @Test
    @DisplayName("사용 재고가 소진되면 실패한다")
    void isSatisfied_usageStockExhausted() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, 5L, null),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            Instant.parse("2026-01-02T00:00:00Z"),
            5L,
            null,
            null,
            null,
            null,
            null
        )).isFalse();
    }

    @Test
    @DisplayName("사용 재고가 남아 있으면 통과한다")
    void isSatisfied_usageStockRemaining() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, 5L, null),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            Instant.parse("2026-01-02T00:00:00Z"),
            4L,
            null,
            null,
            null,
            null,
            null
        )).isTrue();
    }

    @Test
    @DisplayName("최소 결제 금액 미만이면 실패한다")
    void isSatisfied_minPaymentBelow() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, null, new BigDecimal("10000")),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            Instant.parse("2026-01-02T00:00:00Z"),
            0L,
            new BigDecimal("9999"),
            null,
            null,
            null,
            null
        )).isFalse();
    }

    @Test
    @DisplayName("결제 금액이 null 이면 최소 금액 검사에서 실패한다")
    void isSatisfied_minPaymentNullAmount() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, null, new BigDecimal("10000")),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            Instant.parse("2026-01-02T00:00:00Z"),
            0L,
            null,
            null,
            null,
            null,
            null
        )).isFalse();
    }

    @Test
    @DisplayName("최소 결제 금액 이상이면 통과한다")
    void isSatisfied_minPaymentOk() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, null, new BigDecimal("10000")),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            Instant.parse("2026-01-02T00:00:00Z"),
            0L,
            new BigDecimal("10000"),
            null,
            null,
            null,
            null
        )).isTrue();
    }

    @Test
    @DisplayName("적용 조건이 불만족이면 실패한다")
    void isSatisfied_applyNotSatisfied() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, null, null),
            CouponApplyCondition.create(List.of("p1"), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            Instant.parse("2026-01-02T00:00:00Z"),
            0L,
            null,
            "p2",
            null,
            null,
            null
        )).isFalse();
    }

    @Test
    @DisplayName("적용 조건이 만족되면 통과한다")
    void isSatisfied_applySatisfied() {
        final CouponUsageDomain domain = CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, null, null),
            CouponApplyCondition.create(List.of("p1"), List.of(), List.of(), null)
        );
        assertThat(domain.isSatisfied(
            Instant.parse("2026-01-01T00:00:00Z"),
            null,
            Instant.parse("2026-01-02T00:00:00Z"),
            0L,
            null,
            "p1",
            null,
            null,
            null
        )).isTrue();
    }
}

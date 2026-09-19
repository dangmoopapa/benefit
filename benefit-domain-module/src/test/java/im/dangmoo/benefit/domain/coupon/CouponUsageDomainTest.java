package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponApplyCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageCondition;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponUsageValidityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponUsageDomainTest {

    private static final Instant ISSUED_AT = Instant.parse("2024-06-01T00:00:00Z");
    private static final Instant NOW = Instant.parse("2024-06-05T00:00:00Z");

    private static CouponUsageDomain domain(
        final CouponUsageValidityType validityType,
        final Instant startAt,
        final Instant endAt,
        final Integer daysAfterIssue,
        final Long stockQuantity,
        final BigDecimal minPaymentAmount
    ) {
        return CouponUsageDomain.of(
            CouponUsageCondition.create(
                validityType,
                startAt,
                endAt,
                daysAfterIssue,
                stockQuantity,
                minPaymentAmount
            ),
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null)
        );
    }

    private static CouponUsageDomain withApply(
        final List<String> productIds,
        final String segmentId
    ) {
        return CouponUsageDomain.of(
            CouponUsageCondition.create(null, null, null, null, null, null),
            CouponApplyCondition.create(productIds, List.of(), List.of(), segmentId)
        );
    }

    @Nested
    @DisplayName("resolveExpiresAt")
    class ResolveExpiresAt {

        @Test
        @DisplayName("validityType 이 null 이면 null 을 반환한다")
        void nullValidity_null() {
            final CouponUsageDomain domain = domain(null, null, null, 7, null, null);
            assertThat(domain.resolveExpiresAt(ISSUED_AT)).isNull();
        }

        @Test
        @DisplayName("FIXED_PERIOD 는 endAt 을 반환한다")
        void fixedPeriod_returnsEndAt() {
            final Instant endAt = Instant.parse("2024-12-31T00:00:00Z");
            final CouponUsageDomain domain = domain(
                CouponUsageValidityType.FIXED_PERIOD,
                Instant.parse("2024-01-01T00:00:00Z"),
                endAt,
                99,
                null,
                null
            );
            assertThat(domain.resolveExpiresAt(ISSUED_AT)).isEqualTo(endAt);
        }

        @Test
        @DisplayName("UNTIL_DAYS_AFTER_ISSUE 는 issuedAt + days 이다")
        void untilDays_addsDays() {
            final CouponUsageDomain domain = domain(
                CouponUsageValidityType.UNTIL_DAYS_AFTER_ISSUE,
                null,
                null,
                7,
                null,
                null
            );
            assertThat(domain.resolveExpiresAt(ISSUED_AT))
                .isEqualTo(ISSUED_AT.plus(7, ChronoUnit.DAYS));
        }

        @Test
        @DisplayName("FOR_DAYS_AFTER_ISSUE 도 issuedAt + days 이다")
        void forDays_addsDays() {
            final CouponUsageDomain domain = domain(
                CouponUsageValidityType.FOR_DAYS_AFTER_ISSUE,
                null,
                null,
                3,
                null,
                null
            );
            assertThat(domain.resolveExpiresAt(ISSUED_AT))
                .isEqualTo(ISSUED_AT.plus(3, ChronoUnit.DAYS));
        }

        @ParameterizedTest
        @EnumSource(value = CouponUsageValidityType.class, names = {"UNTIL_DAYS_AFTER_ISSUE", "FOR_DAYS_AFTER_ISSUE"})
        @DisplayName("daysAfterIssue 가 null 이면 null 을 반환한다")
        void daysNull_returnsNull(final CouponUsageValidityType type) {
            final CouponUsageDomain domain = domain(type, null, null, null, null, null);
            assertThat(domain.resolveExpiresAt(ISSUED_AT)).isNull();
        }
    }

    @Nested
    @DisplayName("isSatisfied")
    class IsSatisfied {

        @Test
        @DisplayName("expiresAt 이후면 실패한다")
        void afterExpiresAt_false() {
            final CouponUsageDomain domain = domain(null, null, null, null, null, null);
            final Instant expiresAt = NOW.minusSeconds(1);
            assertThat(domain.isSatisfied(ISSUED_AT, expiresAt, NOW, 0, null, null, null, null, null))
                .isFalse();
        }

        @Test
        @DisplayName("expiresAt 과 같으면 통과한다")
        void atExpiresAt_true() {
            final CouponUsageDomain domain = domain(null, null, null, null, null, null);
            assertThat(domain.isSatisfied(ISSUED_AT, NOW, NOW, 0, null, null, null, null, null))
                .isTrue();
        }

        @Test
        @DisplayName("FIXED_PERIOD 시작 전이면 실패한다")
        void fixedPeriod_beforeStart_false() {
            final Instant start = Instant.parse("2024-06-10T00:00:00Z");
            final Instant end = Instant.parse("2024-06-20T00:00:00Z");
            final CouponUsageDomain domain = domain(
                CouponUsageValidityType.FIXED_PERIOD,
                start,
                end,
                null,
                null,
                null
            );
            assertThat(domain.isSatisfied(
                ISSUED_AT,
                end,
                Instant.parse("2024-06-09T00:00:00Z"),
                0,
                null,
                null,
                null,
                null,
                null
            )).isFalse();
        }

        @Test
        @DisplayName("FIXED_PERIOD 종료 후면 실패한다")
        void fixedPeriod_afterEnd_false() {
            final Instant start = Instant.parse("2024-06-01T00:00:00Z");
            final Instant end = Instant.parse("2024-06-04T00:00:00Z");
            final CouponUsageDomain domain = domain(
                CouponUsageValidityType.FIXED_PERIOD,
                start,
                end,
                null,
                null,
                null
            );
            assertThat(domain.isSatisfied(ISSUED_AT, end, NOW, 0, null, null, null, null, null))
                .isFalse();
        }

        @Test
        @DisplayName("FIXED_PERIOD 기간 안이면 통과한다")
        void fixedPeriod_inRange_true() {
            final Instant start = Instant.parse("2024-06-01T00:00:00Z");
            final Instant end = Instant.parse("2024-06-10T00:00:00Z");
            final CouponUsageDomain domain = domain(
                CouponUsageValidityType.FIXED_PERIOD,
                start,
                end,
                null,
                null,
                null
            );
            assertThat(domain.isSatisfied(ISSUED_AT, end, NOW, 0, null, null, null, null, null))
                .isTrue();
        }

        @Test
        @DisplayName("발급 후 N일 타입이 만료되면 실패한다")
        void daysAfterIssue_expired_false() {
            final CouponUsageDomain domain = domain(
                CouponUsageValidityType.UNTIL_DAYS_AFTER_ISSUE,
                null,
                null,
                3,
                null,
                null
            );
            final Instant issued = Instant.parse("2024-06-01T00:00:00Z");
            final Instant now = Instant.parse("2024-06-05T00:00:00Z");
            assertThat(domain.isSatisfied(issued, null, now, 0, null, null, null, null, null))
                .isFalse();
        }

        @Test
        @DisplayName("발급 후 N일 타입이 유효하면 통과한다")
        void daysAfterIssue_valid_true() {
            final CouponUsageDomain domain = domain(
                CouponUsageValidityType.FOR_DAYS_AFTER_ISSUE,
                null,
                null,
                10,
                null,
                null
            );
            assertThat(domain.isSatisfied(ISSUED_AT, null, NOW, 0, null, null, null, null, null))
                .isTrue();
        }

        @Test
        @DisplayName("사용 재고가 소진되면 실패한다")
        void usageStockExhausted_false() {
            final CouponUsageDomain domain = domain(null, null, null, null, 5L, null);
            assertThat(domain.isSatisfied(ISSUED_AT, null, NOW, 5, BigDecimal.TEN, null, null, null, null))
                .isFalse();
        }

        @Test
        @DisplayName("사용 재고가 남아 있으면 통과한다")
        void usageStockRemaining_true() {
            final CouponUsageDomain domain = domain(null, null, null, null, 5L, null);
            assertThat(domain.isSatisfied(ISSUED_AT, null, NOW, 4, BigDecimal.TEN, null, null, null, null))
                .isTrue();
        }

        @Test
        @DisplayName("최소 결제금액 미달이면 실패한다")
        void minPayment_below_false() {
            final CouponUsageDomain domain = domain(null, null, null, null, null, new BigDecimal("10000"));
            assertThat(domain.isSatisfied(
                ISSUED_AT,
                null,
                NOW,
                0,
                new BigDecimal("9999"),
                null,
                null,
                null,
                null
            )).isFalse();
        }

        @Test
        @DisplayName("결제금액이 null 이면 최소 결제금액 검사에서 실패한다")
        void minPayment_nullAmount_false() {
            final CouponUsageDomain domain = domain(null, null, null, null, null, new BigDecimal("10000"));
            assertThat(domain.isSatisfied(ISSUED_AT, null, NOW, 0, null, null, null, null, null))
                .isFalse();
        }

        @Test
        @DisplayName("최소 결제금액 이상이면 통과한다")
        void minPayment_ok_true() {
            final CouponUsageDomain domain = domain(null, null, null, null, null, new BigDecimal("10000"));
            assertThat(domain.isSatisfied(
                ISSUED_AT,
                null,
                NOW,
                0,
                new BigDecimal("10000"),
                null,
                null,
                null,
                null
            )).isTrue();
        }

        @Test
        @DisplayName("적용 조건을 만족하지 않으면 실패한다")
        void applyNotSatisfied_false() {
            final CouponUsageDomain domain = withApply(List.of("p1"), null);
            assertThat(domain.isSatisfied(ISSUED_AT, null, NOW, 0, null, "p2", null, null, null))
                .isFalse();
        }

        @Test
        @DisplayName("적용 조건까지 모두 통과하면 성공한다")
        void applySatisfied_true() {
            final CouponUsageDomain domain = withApply(List.of("p1"), "seg");
            assertThat(domain.isSatisfied(ISSUED_AT, null, NOW, 0, null, "p1", null, null, "seg"))
                .isTrue();
        }
    }
}

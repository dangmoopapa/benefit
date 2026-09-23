package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyType;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponAccountCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponApplyCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponBenefitCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponIssueCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponIssueFrequency;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponLifecycleCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponUsageCondition;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponUsageValidityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponIssueDomainTest {

    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");

    @Test
    @DisplayName("발급 가능하면 ISSUABLE")
    void issuability_issuable() {
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(
            activePolicy(issuedAnyTime(), usableAnyTime())
        );
        assertThat(couponIssue.issuabilityAt(NOW, false, 0L))
            .isEqualTo(CouponIssueDomain.Issuability.ISSUABLE);
    }

    @Test
    @DisplayName("정책이 활성이 아니면 POLICY_INACTIVE")
    void issuability_policyInactive() {
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(
            policy(CouponPolicyStatus.DRAFT, issuedAnyTime(), usableAnyTime(), false, false)
        );
        assertThat(couponIssue.issuabilityAt(NOW, false, 0L))
            .isEqualTo(CouponIssueDomain.Issuability.POLICY_INACTIVE);
    }

    @Test
    @DisplayName("이미 발급했으면 ALREADY_ISSUED")
    void issuability_alreadyIssued() {
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(
            activePolicy(issuedAnyTime(), usableAnyTime())
        );
        assertThat(couponIssue.issuabilityAt(NOW, true, 0L))
            .isEqualTo(CouponIssueDomain.Issuability.ALREADY_ISSUED);
    }

    @Test
    @DisplayName("재고가 남지 않았으면 STOCK_EXHAUSTED")
    void issuability_stockExhausted() {
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(
            activePolicy(issuedWithStock(10L), usableAnyTime())
        );
        assertThat(couponIssue.issuabilityAt(NOW, false, 10L))
            .isEqualTo(CouponIssueDomain.Issuability.STOCK_EXHAUSTED);
    }

    @Test
    @DisplayName("발급 기간 밖이면 OUT_OF_PERIOD")
    void issuability_outOfPeriod() {
        final Instant startAt = LocalDateTime.of(2026, 7, 1, 0, 0).toInstant(ZoneOffset.UTC);
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(
            activePolicy(issuedFrom(startAt), usableAnyTime())
        );
        assertThat(couponIssue.issuabilityAt(NOW, false, 0L))
            .isEqualTo(CouponIssueDomain.Issuability.OUT_OF_PERIOD);
    }

    @Test
    @DisplayName("허용 요일이면 열려 있다")
    void isOpenAt_allowedDayOfWeek() {
        final Instant monday = LocalDateTime.of(2024, 6, 10, 12, 0).toInstant(ZoneOffset.UTC);
        final CouponIssueCondition issuedOnMonday = CouponIssueCondition.create(
            null, null, null, List.of(DayOfWeek.MONDAY), List.of()
        );
        assertThat(CouponIssueDomain.of(activePolicy(issuedOnMonday, usableAnyTime())).isOpenAt(monday))
            .isTrue();
    }

    @Test
    @DisplayName("허용 시간이 아니면 닫혀 있다")
    void isOpenAt_deniedHour() {
        final Instant noon = LocalDateTime.of(2024, 6, 10, 12, 0).toInstant(ZoneOffset.UTC);
        final CouponIssueCondition issuedAtMorning = CouponIssueCondition.create(
            null, null, null, List.of(), List.of(10, 11)
        );
        assertThat(CouponIssueDomain.of(activePolicy(issuedAtMorning, usableAnyTime())).isOpenAt(noon))
            .isFalse();
    }

    @Test
    @DisplayName("시작 시각과 같으면 열려 있고, 종료 시각 다음은 닫혀 있다")
    void isOpenAt_periodBounds() {
        final Instant startAt = Instant.parse("2026-06-01T00:00:00Z");
        final Instant endAt = Instant.parse("2026-06-30T00:00:00Z");
        final CouponIssueCondition issuedInJune = CouponIssueCondition.create(
            startAt, endAt, null, List.of(), List.of()
        );
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(activePolicy(issuedInJune, usableAnyTime()));
        assertThat(couponIssue.isOpenAt(startAt)).isTrue();
        assertThat(couponIssue.isOpenAt(endAt)).isTrue();
        assertThat(couponIssue.isOpenAt(startAt.minusSeconds(1))).isFalse();
        assertThat(couponIssue.isOpenAt(endAt.plusSeconds(1))).isFalse();
    }

    @Test
    @DisplayName("발급키는 발급 주기를 반영한다")
    void issueKeyFor_monthly() {
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(
            activePolicy(issuedEvery(CouponIssueFrequency.ONCE_PER_MONTH), usableAnyTime())
        );
        assertThat(couponIssue.issueKeyFor("p1", "u1", Instant.parse("2026-09-23T15:00:00Z")))
            .isEqualTo("p1:u1:2026-09");
    }

    @Test
    @DisplayName("발급 주기가 없으면 ONCE_PER_USER 로 취급한다")
    void issueKeyFor_nullFrequency() {
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(
            activePolicy(issuedAnyTime(), usableAnyTime())
        );
        assertThat(couponIssue.issueKeyFor("p", "u", NOW)).isEqualTo("p:u");
    }

    @Test
    @DisplayName("만료일은 발급 후 N일이다")
    void expiresAtFrom_daysAfterIssue() {
        final CouponUsageCondition usableForSevenDays = CouponUsageCondition.create(
            CouponUsageValidityType.FOR_DAYS_AFTER_ISSUE, null, null, 7, null, null
        );
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(
            activePolicy(issuedAnyTime(), usableForSevenDays)
        );
        assertThat(couponIssue.expiresAtFrom(Instant.parse("2026-06-15T00:00:00Z")))
            .isEqualTo(Instant.parse("2026-06-22T00:00:00Z"));
    }

    @Test
    @DisplayName("사용 기간 조건이 없으면 만료일이 없다")
    void expiresAtFrom_noValidity() {
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(
            activePolicy(issuedAnyTime(), usableAnyTime())
        );
        assertThat(couponIssue.expiresAtFrom(NOW)).isNull();
    }

    @Test
    @DisplayName("이번 발급으로 재고가 딱 소진되면 마지막 발급이다")
    void isLastIssue() {
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(
            activePolicy(issuedWithStock(5L), usableAnyTime())
        );
        assertThat(couponIssue.isLastIssue(5L)).isTrue();
        assertThat(couponIssue.isLastIssue(4L)).isFalse();
    }

    @Test
    @DisplayName("회수 또는 결제 취소 회수가 열려 있으면 사용 후 회수할 수 있다")
    void isRecoverableAfterUse() {
        assertThat(CouponIssueDomain.of(
            policy(CouponPolicyStatus.ACTIVE, issuedAnyTime(), usableAnyTime(), true, false)
        ).isRecoverableAfterUse()).isTrue();
        assertThat(CouponIssueDomain.of(
            policy(CouponPolicyStatus.ACTIVE, issuedAnyTime(), usableAnyTime(), false, true)
        ).isRecoverableAfterUse()).isTrue();
        assertThat(CouponIssueDomain.of(
            policy(CouponPolicyStatus.ACTIVE, issuedAnyTime(), usableAnyTime(), false, false)
        ).isRecoverableAfterUse()).isFalse();
    }

    @Test
    @DisplayName("재고 수량을 그대로 노출한다")
    void stockQuantity() {
        assertThat(CouponIssueDomain.of(activePolicy(issuedWithStock(7L), usableAnyTime())).stockQuantity())
            .isEqualTo(7L);
        assertThat(CouponIssueDomain.of(activePolicy(issuedAnyTime(), usableAnyTime())).stockQuantity())
            .isNull();
    }

    private static CouponPolicyCache activePolicy(
        final CouponIssueCondition issueCondition,
        final CouponUsageCondition usageCondition
    ) {
        return policy(CouponPolicyStatus.ACTIVE, issueCondition, usageCondition, false, false);
    }

    private static CouponPolicyCache policy(
        final CouponPolicyStatus status,
        final CouponIssueCondition issueCondition,
        final CouponUsageCondition usageCondition,
        final boolean reclaimableOnPaymentCancel,
        final boolean reclaimable
    ) {
        final Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        return new CouponPolicyCache(
            "id",
            "가입 축하 쿠폰",
            "가입 시 1회 발급",
            "user.join",
            CouponPolicyType.MARKETING,
            status,
            CouponBenefitCondition.create(BigDecimal.TEN, null, null),
            issueCondition,
            usageCondition,
            CouponApplyCondition.create(List.of(), List.of(), List.of(), null),
            CouponLifecycleCondition.create(reclaimableOnPaymentCancel, reclaimable),
            CouponAccountCondition.create(null),
            "system",
            createdAt,
            "system",
            createdAt
        );
    }

    private static CouponIssueCondition issuedAnyTime() {
        return CouponIssueCondition.create(null, null, null, List.of(), List.of());
    }

    private static CouponIssueCondition issuedWithStock(final long stockQuantity) {
        return CouponIssueCondition.create(
            null, null, stockQuantity, List.of(), List.of(), CouponIssueFrequency.ONCE_PER_USER
        );
    }

    private static CouponIssueCondition issuedFrom(final Instant startAt) {
        return CouponIssueCondition.create(
            startAt, null, null, List.of(), List.of(), CouponIssueFrequency.ONCE_PER_USER
        );
    }

    private static CouponIssueCondition issuedEvery(final CouponIssueFrequency frequency) {
        return CouponIssueCondition.create(null, null, null, List.of(), List.of(), frequency);
    }

    private static CouponUsageCondition usableAnyTime() {
        return CouponUsageCondition.create(null, null, null, null, null, null);
    }
}

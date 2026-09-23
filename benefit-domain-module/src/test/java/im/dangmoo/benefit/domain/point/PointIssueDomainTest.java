package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.data.entity.point.policy.PointPolicyStatus;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointAccountCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointBenefitCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointBenefitType;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointExpireCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointExpireType;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointIssueCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointIssueFrequency;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointLifecycleCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PointIssueDomainTest {

    private static final Instant NOW = Instant.parse("2026-06-15T12:00:00Z");

    @Test
    @DisplayName("지급 가능하면 ISSUABLE")
    void issuability_issuable() {
        final PointIssueDomain pointIssue = PointIssueDomain.of(activePolicy(issuedAnyTime()));
        assertThat(pointIssue.issuabilityAt(NOW, false, 0L))
            .isEqualTo(PointIssueDomain.Issuability.ISSUABLE);
    }

    @Test
    @DisplayName("정책이 활성이 아니면 POLICY_INACTIVE")
    void issuability_policyInactive() {
        final PointIssueDomain pointIssue = PointIssueDomain.of(draftPolicy());
        assertThat(pointIssue.issuabilityAt(NOW, false, 0L))
            .isEqualTo(PointIssueDomain.Issuability.POLICY_INACTIVE);
    }

    @Test
    @DisplayName("이미 지급했으면 ALREADY_GRANTED")
    void issuability_alreadyGranted() {
        final PointIssueDomain pointIssue = PointIssueDomain.of(activePolicy(issuedAnyTime()));
        assertThat(pointIssue.issuabilityAt(NOW, true, 0L))
            .isEqualTo(PointIssueDomain.Issuability.ALREADY_GRANTED);
    }

    @Test
    @DisplayName("재고가 남지 않았으면 STOCK_EXHAUSTED")
    void issuability_stockExhausted() {
        final PointIssueDomain pointIssue = PointIssueDomain.of(activePolicy(issuedWithStock(10L)));
        assertThat(pointIssue.issuabilityAt(NOW, false, 10L))
            .isEqualTo(PointIssueDomain.Issuability.STOCK_EXHAUSTED);
    }

    @Test
    @DisplayName("지급 기간 밖이면 OUT_OF_PERIOD")
    void issuability_outOfPeriod() {
        final Instant startAt = LocalDateTime.of(2026, 7, 1, 0, 0).toInstant(ZoneOffset.UTC);
        final PointIssueDomain pointIssue = PointIssueDomain.of(activePolicy(issuedFrom(startAt)));
        assertThat(pointIssue.issuabilityAt(NOW, false, 0L))
            .isEqualTo(PointIssueDomain.Issuability.OUT_OF_PERIOD);
    }

    @Test
    @DisplayName("회수 가능한 정책에서 잔액 안의 금액이면 RECLAIMABLE")
    void reclaimability_reclaimable() {
        final PointIssueDomain pointIssue = PointIssueDomain.of(reclaimablePolicy());
        assertThat(pointIssue.reclaimabilityOf(100L, 100L))
            .isEqualTo(PointIssueDomain.Reclaimability.RECLAIMABLE);
    }

    @Test
    @DisplayName("회수 불가 정책이면 POLICY_NOT_RECLAIMABLE")
    void reclaimability_policyNotReclaimable() {
        final PointIssueDomain pointIssue = PointIssueDomain.of(activePolicy(issuedAnyTime()));
        assertThat(pointIssue.reclaimabilityOf(100L, 100L))
            .isEqualTo(PointIssueDomain.Reclaimability.POLICY_NOT_RECLAIMABLE);
    }

    @Test
    @DisplayName("회수 금액이 0 이하면 INVALID_AMOUNT")
    void reclaimability_invalidAmount() {
        final PointIssueDomain pointIssue = PointIssueDomain.of(reclaimablePolicy());
        assertThat(pointIssue.reclaimabilityOf(0L, 100L))
            .isEqualTo(PointIssueDomain.Reclaimability.INVALID_AMOUNT);
    }

    @Test
    @DisplayName("회수 금액이 잔액보다 크면 INSUFFICIENT_BALANCE")
    void reclaimability_insufficientBalance() {
        final PointIssueDomain pointIssue = PointIssueDomain.of(reclaimablePolicy());
        assertThat(pointIssue.reclaimabilityOf(101L, 100L))
            .isEqualTo(PointIssueDomain.Reclaimability.INSUFFICIENT_BALANCE);
    }

    @Test
    @DisplayName("기간·요일·시간 제한이 없으면 항상 열려 있다")
    void isOpenAt_unrestricted() {
        assertThat(PointIssueDomain.of(activePolicy(issuedAnyTime())).isOpenAt(NOW)).isTrue();
    }

    @Test
    @DisplayName("허용 요일이면 열려 있다")
    void isOpenAt_allowedDayOfWeek() {
        final Instant monday = LocalDateTime.of(2024, 6, 10, 12, 0).toInstant(ZoneOffset.UTC);
        final PointIssueCondition issuedOnMonday = PointIssueCondition.create(
            null, null, null, List.of(DayOfWeek.MONDAY), List.of(), PointIssueFrequency.ONCE_PER_USER
        );
        assertThat(PointIssueDomain.of(activePolicy(issuedOnMonday)).isOpenAt(monday)).isTrue();
    }

    @Test
    @DisplayName("허용 요일이 아니면 닫혀 있다")
    void isOpenAt_deniedDayOfWeek() {
        final Instant monday = LocalDateTime.of(2024, 6, 10, 12, 0).toInstant(ZoneOffset.UTC);
        final PointIssueCondition issuedOnTuesday = PointIssueCondition.create(
            null, null, null, List.of(DayOfWeek.TUESDAY), List.of(), PointIssueFrequency.ONCE_PER_USER
        );
        assertThat(PointIssueDomain.of(activePolicy(issuedOnTuesday)).isOpenAt(monday)).isFalse();
    }

    @Test
    @DisplayName("허용 시간이 아니면 닫혀 있다")
    void isOpenAt_deniedHour() {
        final Instant noon = LocalDateTime.of(2024, 6, 10, 12, 0).toInstant(ZoneOffset.UTC);
        final PointIssueCondition issuedAtMorning = PointIssueCondition.create(
            null, null, null, List.of(), List.of(10, 11), PointIssueFrequency.ONCE_PER_USER
        );
        assertThat(PointIssueDomain.of(activePolicy(issuedAtMorning)).isOpenAt(noon)).isFalse();
    }

    @Test
    @DisplayName("시작 시각과 같으면 열려 있고, 종료 시각 다음은 닫혀 있다")
    void isOpenAt_periodBounds() {
        final Instant startAt = Instant.parse("2026-06-01T00:00:00Z");
        final Instant endAt = Instant.parse("2026-06-30T00:00:00Z");
        final PointIssueCondition issuedInJune = PointIssueCondition.create(
            startAt, endAt, null, List.of(), List.of(), PointIssueFrequency.ONCE_PER_USER
        );
        final PointIssueDomain pointIssue = PointIssueDomain.of(activePolicy(issuedInJune));
        assertThat(pointIssue.isOpenAt(startAt)).isTrue();
        assertThat(pointIssue.isOpenAt(endAt)).isTrue();
        assertThat(pointIssue.isOpenAt(startAt.minusSeconds(1))).isFalse();
        assertThat(pointIssue.isOpenAt(endAt.plusSeconds(1))).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
        "policy-1, user-1, GRANT:policy-1:user-1",
        "p, u, GRANT:p:u"
    })
    @DisplayName("ONCE_PER_USER 지급키는 GRANT:policyId:userId 형식이다")
    void grantKeyFor_oncePerUser(final String policyId, final String userId, final String expected) {
        final PointIssueDomain pointIssue = PointIssueDomain.of(
            activePolicy(issuedEvery(PointIssueFrequency.ONCE_PER_USER))
        );
        assertThat(pointIssue.grantKeyFor(policyId, userId, NOW)).isEqualTo(expected);
    }

    @Test
    @DisplayName("지급 주기가 없으면 ONCE_PER_USER 로 취급한다")
    void grantKeyFor_nullFrequency() {
        final PointIssueDomain pointIssue = PointIssueDomain.of(activePolicy(issuedAnyTime()));
        assertThat(pointIssue.grantKeyFor("p", "u", NOW)).isEqualTo("GRANT:p:u");
    }

    @Test
    @DisplayName("ONCE_PER_DAY 지급키는 UTC yyyy-MM-dd 를 붙인다")
    void grantKeyFor_oncePerDay() {
        final Instant grantedAt = LocalDateTime.of(2026, 9, 23, 12, 0).toInstant(ZoneOffset.UTC);
        final PointIssueDomain pointIssue = PointIssueDomain.of(
            activePolicy(issuedEvery(PointIssueFrequency.ONCE_PER_DAY))
        );
        assertThat(pointIssue.grantKeyFor("p1", "u1", grantedAt)).isEqualTo("GRANT:p1:u1:2026-09-23");
    }

    @Test
    @DisplayName("ONCE_PER_MONTH 지급키는 UTC yyyy-MM 를 붙인다")
    void grantKeyFor_oncePerMonth() {
        final Instant grantedAt = LocalDateTime.of(2026, 9, 23, 12, 0).toInstant(ZoneOffset.UTC);
        final PointIssueDomain pointIssue = PointIssueDomain.of(
            activePolicy(issuedEvery(PointIssueFrequency.ONCE_PER_MONTH))
        );
        assertThat(pointIssue.grantKeyFor("p1", "u1", grantedAt)).isEqualTo("GRANT:p1:u1:2026-09");
    }

    @Test
    @DisplayName("ONCE_PER_YEAR 지급키는 UTC yyyy 를 붙인다")
    void grantKeyFor_oncePerYear() {
        final Instant grantedAt = LocalDateTime.of(2026, 12, 31, 23, 0).toInstant(ZoneOffset.UTC);
        final PointIssueDomain pointIssue = PointIssueDomain.of(
            activePolicy(issuedEvery(PointIssueFrequency.ONCE_PER_YEAR))
        );
        assertThat(pointIssue.grantKeyFor("p1", "u1", grantedAt)).isEqualTo("GRANT:p1:u1:2026");
    }

    @Test
    @DisplayName("재고 수량을 그대로 노출한다")
    void stockQuantity() {
        assertThat(PointIssueDomain.of(activePolicy(issuedWithStock(7L))).stockQuantity()).isEqualTo(7L);
        assertThat(PointIssueDomain.of(activePolicy(issuedAnyTime())).stockQuantity()).isNull();
    }

    private static PointPolicyDocument activePolicy(final PointIssueCondition issueCondition) {
        return policy(fixedAmount(100L), issueCondition, PointLifecycleCondition.create(false))
            .changeStatus(PointPolicyStatus.ACTIVE, "system");
    }

    private static PointPolicyDocument reclaimablePolicy() {
        return policy(fixedAmount(100L), issuedAnyTime(), PointLifecycleCondition.create(true))
            .changeStatus(PointPolicyStatus.ACTIVE, "system");
    }

    private static PointPolicyDocument draftPolicy() {
        return policy(fixedAmount(100L), issuedAnyTime(), PointLifecycleCondition.create(false));
    }

    private static PointPolicyDocument policy(
        final PointBenefitCondition benefitCondition,
        final PointIssueCondition issueCondition,
        final PointLifecycleCondition lifecycleCondition
    ) {
        return PointPolicyDocument.create(
            "가입 축하 포인트",
            "가입 시 1회 지급",
            "user.join",
            benefitCondition,
            issueCondition,
            PointExpireCondition.create(PointExpireType.NEVER, null, null),
            lifecycleCondition,
            PointAccountCondition.create(null),
            "system"
        );
    }

    private static PointIssueCondition issuedAnyTime() {
        return PointIssueCondition.create(null, null, null, List.of(), List.of());
    }

    private static PointIssueCondition issuedWithStock(final long stockQuantity) {
        return PointIssueCondition.create(
            null, null, stockQuantity, List.of(), List.of(), PointIssueFrequency.ONCE_PER_USER
        );
    }

    private static PointIssueCondition issuedFrom(final Instant startAt) {
        return PointIssueCondition.create(
            startAt, null, null, List.of(), List.of(), PointIssueFrequency.ONCE_PER_USER
        );
    }

    private static PointIssueCondition issuedEvery(final PointIssueFrequency frequency) {
        return PointIssueCondition.create(null, null, null, List.of(), List.of(), frequency);
    }

    private static PointBenefitCondition fixedAmount(final long amount) {
        return PointBenefitCondition.create(PointBenefitType.FIXED, amount, null, null, null, null);
    }
}

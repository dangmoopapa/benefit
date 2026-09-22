package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointIssueCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PointIssueDomainTest {

    private static final ZoneId UTC = ZoneOffset.UTC;

    @Test
    @DisplayName("기간·요일·시간 제한이 없으면 항상 통과한다")
    void isSatisfiedAt_unrestricted() {
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(null, null, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfiedAt(Instant.parse("2024-06-15T00:00:00Z"))).isTrue();
    }

    @Test
    @DisplayName("startAt 이전이면 실패한다")
    void isSatisfiedAt_beforeStartAt() {
        final Instant start = LocalDateTime.of(2024, 6, 10, 0, 0).atZone(UTC).toInstant();
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(start, null, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 9, 23, 0).atZone(UTC).toInstant())).isFalse();
    }

    @Test
    @DisplayName("startAt 과 같으면 통과한다")
    void isSatisfiedAt_atStartAt() {
        final Instant start = LocalDateTime.of(2024, 6, 10, 0, 0).atZone(UTC).toInstant();
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(start, null, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfiedAt(start)).isTrue();
    }

    @Test
    @DisplayName("endAt 이후면 실패한다")
    void isSatisfiedAt_afterEndAt() {
        final Instant end = LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant();
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(null, end, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfiedAt(end.plusSeconds(1))).isFalse();
    }

    @Test
    @DisplayName("endAt 과 같으면 통과한다")
    void isSatisfiedAt_atEndAt() {
        final Instant end = LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant();
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(null, end, null, List.of(), List.of())
        );
        assertThat(domain.isSatisfiedAt(end)).isTrue();
    }

    @Test
    @DisplayName("허용 요일이 아니면 실패한다")
    void isSatisfiedAt_dayDenied() {
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(null, null, null, List.of(DayOfWeek.TUESDAY), List.of())
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isFalse();
    }

    @Test
    @DisplayName("허용 요일이면 통과한다")
    void isSatisfiedAt_dayAllowed() {
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(null, null, null, List.of(DayOfWeek.MONDAY), List.of())
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isTrue();
    }

    @Test
    @DisplayName("허용 시간이 아니면 실패한다")
    void isSatisfiedAt_hourDenied() {
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(null, null, null, List.of(), List.of(10, 11))
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isFalse();
    }

    @Test
    @DisplayName("허용 시간이면 통과한다")
    void isSatisfiedAt_hourAllowed() {
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(null, null, null, List.of(), List.of(12))
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isTrue();
    }

    @Test
    @DisplayName("기간·요일·시간을 모두 만족해야 통과한다")
    void isSatisfiedAt_combined() {
        final Instant start = LocalDateTime.of(2024, 6, 10, 0, 0).atZone(UTC).toInstant();
        final Instant end = LocalDateTime.of(2024, 6, 10, 23, 0).atZone(UTC).toInstant();
        final PointIssueDomain domain = PointIssueDomain.of(
            PointIssueCondition.create(start, end, null, List.of(DayOfWeek.MONDAY), List.of(12))
        );
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 12, 0).atZone(UTC).toInstant())).isTrue();
        assertThat(domain.isSatisfiedAt(LocalDateTime.of(2024, 6, 10, 13, 0).atZone(UTC).toInstant())).isFalse();
    }
}

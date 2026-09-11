package im.dangmoo.benefit.domain.data.point.policy.issue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PointIssueRepeatTest {

    private static final Instant AT = Instant.parse("2026-09-11T15:00:00Z");

    @Test
    @DisplayName("1회는 유저·정책만으로 키를 만든다")
    void onceIgnoresCalendar() {
        assertThat(PointIssueRepeat.ONCE.idempotencyKey("p1", "u1", AT)).isEqualTo("p1:u1");
    }

    @Test
    @DisplayName("매일은 UTC 날짜를 붙인다")
    void dailyUsesUtcDate() {
        assertThat(PointIssueRepeat.DAILY.idempotencyKey("p1", "u1", AT)).isEqualTo("p1:u1:2026-09-11");
    }

    @Test
    @DisplayName("매달은 UTC 연월을 붙인다")
    void monthlyUsesUtcYearMonth() {
        assertThat(PointIssueRepeat.MONTHLY.idempotencyKey("p1", "u1", AT)).isEqualTo("p1:u1:2026-09");
    }

    @Test
    @DisplayName("매년은 UTC 연도를 붙인다")
    void yearlyUsesUtcYear() {
        assertThat(PointIssueRepeat.YEARLY.idempotencyKey("p1", "u1", AT)).isEqualTo("p1:u1:2026");
    }
}

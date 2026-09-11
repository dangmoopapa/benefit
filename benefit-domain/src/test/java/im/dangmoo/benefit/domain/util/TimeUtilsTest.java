package im.dangmoo.benefit.domain.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class TimeUtilsTest {

    private static final Instant AT = Instant.parse("2026-09-11T15:30:45Z");

    @Test
    @DisplayName("now는 현재 Instant를 반환한다")
    void nowReturnsInstant() {
        final Instant before = Instant.now();
        final Instant now = TimeUtils.now();
        final Instant after = Instant.now();

        assertThat(now).isBetween(before, after);
    }

    @Test
    @DisplayName("UTC ZonedDateTime으로 변환한다")
    void toUtc() {
        assertThat(TimeUtils.toUtc(AT).getOffset()).isEqualTo(ZoneOffset.UTC);
        assertThat(TimeUtils.toUtc(AT).toInstant()).isEqualTo(AT);
    }

    @Test
    @DisplayName("UTC 날짜를 꺼낸다")
    void toUtcDate() {
        assertThat(TimeUtils.toUtcDate(AT)).isEqualTo(LocalDate.of(2026, 9, 11));
    }

    @Test
    @DisplayName("UTC 시각을 꺼낸다")
    void toUtcTime() {
        assertThat(TimeUtils.toUtcTime(AT)).isEqualTo(LocalTime.of(15, 30, 45));
    }

    @Test
    @DisplayName("UTC 연월을 꺼낸다")
    void toUtcYearMonth() {
        assertThat(TimeUtils.toUtcYearMonth(AT)).isEqualTo(YearMonth.of(2026, 9));
    }

    @Test
    @DisplayName("UTC 연도를 꺼낸다")
    void toUtcYear() {
        assertThat(TimeUtils.toUtcYear(AT)).isEqualTo(2026);
    }

    @Test
    @DisplayName("UTC 요일을 꺼낸다")
    void toUtcDayOfWeek() {
        assertThat(TimeUtils.toUtcDayOfWeek(AT)).isEqualTo(DayOfWeek.FRIDAY);
    }

    @Test
    @DisplayName("UTC 기준 N일 뒤 자정을 만든다")
    void startOfUtcDayAfter() {
        assertThat(TimeUtils.startOfUtcDayAfter(AT, 7))
            .isEqualTo(Instant.parse("2026-09-18T00:00:00Z"));
        assertThat(TimeUtils.startOfUtcDayAfter(AT, 0))
            .isEqualTo(Instant.parse("2026-09-11T00:00:00Z"));
    }

    @Test
    @DisplayName("UTC 기준 개월을 더한다")
    void plusUtcMonths() {
        assertThat(TimeUtils.plusUtcMonths(AT, 1))
            .isEqualTo(Instant.parse("2026-10-11T15:30:45Z"));
        assertThat(TimeUtils.plusUtcMonths(Instant.parse("2026-01-31T12:00:00Z"), 1))
            .isEqualTo(Instant.parse("2026-02-28T12:00:00Z"));
    }
}

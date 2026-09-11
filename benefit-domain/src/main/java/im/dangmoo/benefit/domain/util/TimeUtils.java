package im.dangmoo.benefit.domain.util;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class TimeUtils {

    private TimeUtils() {
    }

    public static Instant now() {
        return Instant.now();
    }

    public static ZonedDateTime toUtc(final Instant instant) {
        return instant.atZone(ZoneOffset.UTC);
    }

    public static LocalDate toUtcDate(final Instant instant) {
        return toUtc(instant).toLocalDate();
    }

    public static LocalTime toUtcTime(final Instant instant) {
        return toUtc(instant).toLocalTime();
    }

    public static YearMonth toUtcYearMonth(final Instant instant) {
        return YearMonth.from(toUtc(instant));
    }

    public static int toUtcYear(final Instant instant) {
        return toUtc(instant).getYear();
    }

    public static DayOfWeek toUtcDayOfWeek(final Instant instant) {
        return toUtc(instant).getDayOfWeek();
    }

    public static Instant startOfUtcDayAfter(final Instant instant, final int days) {
        return toUtcDate(instant)
            .plusDays(days)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant();
    }

    public static Instant plusUtcMonths(final Instant instant, final int months) {
        return toUtc(instant).plusMonths(months).toInstant();
    }
}

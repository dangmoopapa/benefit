package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointIssueCondition;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;

public class PointIssueDomain {

    private final Instant startAt;
    private final Instant endAt;
    private final List<DayOfWeek> availableDaysOfWeek;
    private final List<Integer> hours;

    private PointIssueDomain(
        final Instant startAt,
        final Instant endAt,
        final List<DayOfWeek> availableDaysOfWeek,
        final List<Integer> hours
    ) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.availableDaysOfWeek = availableDaysOfWeek;
        this.hours = hours;
    }

    public static PointIssueDomain of(final PointIssueCondition condition) {
        return new PointIssueDomain(
            condition.getStartAt(),
            condition.getEndAt(),
            condition.getAvailableDaysOfWeek(),
            condition.getHours()
        );
    }

    public boolean isSatisfiedAt(final Instant now) {
        if (startAt != null && now.isBefore(startAt)) {
            return false;
        }
        if (endAt != null && now.isAfter(endAt)) {
            return false;
        }
        if (!availableDaysOfWeek.isEmpty()) {
            final DayOfWeek dayOfWeek = now.atZone(PointBalance.ZONE).getDayOfWeek();
            if (!availableDaysOfWeek.contains(dayOfWeek)) {
                return false;
            }
        }
        if (!hours.isEmpty()) {
            final int hour = now.atZone(PointBalance.ZONE).getHour();
            return hours.contains(hour);
        }
        return true;
    }
}

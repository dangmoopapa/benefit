package im.dangmoo.benefit.infrastructure.data.point.policy.condition;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PointIssueCondition {

    private Instant startAt;
    private Instant endAt;
    private Long stockQuantity;
    private List<DayOfWeek> availableDaysOfWeek = new ArrayList<>();
    private List<Integer> hours = new ArrayList<>();
    private PointIssueFrequency frequency;

    private PointIssueCondition() {
    }

    public static PointIssueCondition create(
        final Instant startAt,
        final Instant endAt,
        final Long stockQuantity,
        final List<DayOfWeek> availableDaysOfWeek,
        final List<Integer> hours
    ) {
        return create(startAt, endAt, stockQuantity, availableDaysOfWeek, hours, null);
    }

    public static PointIssueCondition create(
        final Instant startAt,
        final Instant endAt,
        final Long stockQuantity,
        final List<DayOfWeek> availableDaysOfWeek,
        final List<Integer> hours,
        final PointIssueFrequency frequency
    ) {
        final PointIssueCondition condition = new PointIssueCondition();
        condition.startAt = startAt;
        condition.endAt = endAt;
        condition.stockQuantity = stockQuantity;
        condition.availableDaysOfWeek = availableDaysOfWeek == null ? new ArrayList<>() : availableDaysOfWeek;
        condition.hours = hours == null ? new ArrayList<>() : hours;
        condition.frequency = frequency;
        return condition;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public List<DayOfWeek> getAvailableDaysOfWeek() {
        return availableDaysOfWeek;
    }

    public List<Integer> getHours() {
        return hours;
    }

    public PointIssueFrequency getFrequency() {
        return frequency == null ? PointIssueFrequency.ONCE_PER_USER : frequency;
    }
}

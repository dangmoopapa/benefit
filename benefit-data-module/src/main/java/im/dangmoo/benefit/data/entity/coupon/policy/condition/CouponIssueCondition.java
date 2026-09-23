package im.dangmoo.benefit.data.entity.coupon.policy.condition;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CouponIssueCondition {

    private Instant startAt;
    private Instant endAt;
    private Long stockQuantity;
    private List<DayOfWeek> availableDaysOfWeek = new ArrayList<>();
    private List<Integer> hours = new ArrayList<>();
    private CouponIssueFrequency frequency;

    private CouponIssueCondition() {
    }

    public static CouponIssueCondition create(
        final Instant startAt,
        final Instant endAt,
        final Long stockQuantity,
        final List<DayOfWeek> availableDaysOfWeek,
        final List<Integer> hours
    ) {
        return create(startAt, endAt, stockQuantity, availableDaysOfWeek, hours, null);
    }

    public static CouponIssueCondition create(
        final Instant startAt,
        final Instant endAt,
        final Long stockQuantity,
        final List<DayOfWeek> availableDaysOfWeek,
        final List<Integer> hours,
        final CouponIssueFrequency frequency
    ) {
        final CouponIssueCondition condition = new CouponIssueCondition();
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

    public CouponIssueFrequency getFrequency() {
        return frequency == null ? CouponIssueFrequency.ONCE_PER_USER : frequency;
    }
}

package im.dangmoo.benefit.infrastructure.data.coupon.policy.condition;

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

    private CouponIssueCondition() {
    }

    public static CouponIssueCondition create(
        final Instant startAt,
        final Instant endAt,
        final Long stockQuantity,
        final List<DayOfWeek> availableDaysOfWeek,
        final List<Integer> hours
    ) {
        final CouponIssueCondition condition = new CouponIssueCondition();
        condition.startAt = startAt;
        condition.endAt = endAt;
        condition.stockQuantity = stockQuantity;
        condition.availableDaysOfWeek = availableDaysOfWeek == null ? new ArrayList<>() : availableDaysOfWeek;
        condition.hours = hours == null ? new ArrayList<>() : hours;
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
}

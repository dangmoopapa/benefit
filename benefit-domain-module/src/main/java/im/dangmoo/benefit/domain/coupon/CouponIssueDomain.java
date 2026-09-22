package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueCondition;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

public class CouponIssueDomain {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final Instant startAt;
    private final Instant endAt;
    private final Long stockQuantity;
    private final List<DayOfWeek> availableDaysOfWeek;
    private final List<Integer> hours;

    private CouponIssueDomain(
        final Instant startAt,
        final Instant endAt,
        final Long stockQuantity,
        final List<DayOfWeek> availableDaysOfWeek,
        final List<Integer> hours
    ) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.stockQuantity = stockQuantity;
        this.availableDaysOfWeek = availableDaysOfWeek;
        this.hours = hours;
    }

    public static CouponIssueDomain of(final CouponIssueCondition condition) {
        return new CouponIssueDomain(
            condition.getStartAt(),
            condition.getEndAt(),
            condition.getStockQuantity(),
            condition.getAvailableDaysOfWeek(),
            condition.getHours()
        );
    }

    public boolean isSatisfied(final Instant now, final long issuedCount) {
        if (!isSatisfiedAt(now)) {
            return false;
        }
        return !isStockExhausted(issuedCount);
    }

    private boolean isStockExhausted(final long issuedCount) {
        return stockQuantity != null && issuedCount >= stockQuantity;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public boolean isSatisfiedAt(final Instant now) {
        if (startAt != null && now.isBefore(startAt)) {
            return false;
        }
        if (endAt != null && now.isAfter(endAt)) {
            return false;
        }
        if (!availableDaysOfWeek.isEmpty()) {
            final DayOfWeek dayOfWeek = now.atZone(ZONE).getDayOfWeek();
            if (!availableDaysOfWeek.contains(dayOfWeek)) {
                return false;
            }
        }
        if (!hours.isEmpty()) {
            final int hour = now.atZone(ZONE).getHour();
            return hours.contains(hour);
        }
        return true;
    }
}

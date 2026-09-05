package im.dangmoo.benefit.domain.coupon.document.policy.issue;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class CouponIssueCondition {

    private CouponIssuablePeriod period;
    private List<CouponIssuableWeekday> weekdays;
    private List<CouponIssuableTime> timeRanges;
    private String segmentId;
    private Long totalQuantity;

    private CouponIssueCondition() {
    }

    public static CouponIssueCondition create(
        final CouponIssuablePeriod period,
        final List<CouponIssuableWeekday> weekdays,
        final List<CouponIssuableTime> timeRanges,
        final String segmentId,
        final Long totalQuantity
    ) {
        final CouponIssueCondition document = new CouponIssueCondition();
        document.period = period;
        document.weekdays = weekdays;
        document.timeRanges = timeRanges;
        document.segmentId = segmentId;
        document.totalQuantity = totalQuantity;
        return document;
    }

    public boolean isSatisfiedAt(final Instant now, final boolean segmentMatched) {
        if (segmentId != null && !segmentMatched) {
            return false;
        }
        if (period.getStart().isAfter(now) || period.getEnd().isBefore(now)) {
            return false;
        }
        final ZonedDateTime at = now.atZone(ZoneOffset.UTC);
        if (!weekdays.isEmpty() && weekdays.stream().noneMatch(weekday -> weekday.dayOfWeek == at.getDayOfWeek())) {
            return false;
        }
        if (!timeRanges.isEmpty()) {
            final LocalTime time = at.toLocalTime();
            return timeRanges.stream().anyMatch(range ->
                !time.isBefore(range.getStart()) && !time.isAfter(range.getEnd())
            );
        }
        return true;
    }

    public boolean hasRemainingQuantity(final long issuedCount) {
        return totalQuantity == null || issuedCount < totalQuantity;
    }

    public CouponIssuablePeriod getPeriod() {
        return period;
    }

    public List<CouponIssuableWeekday> getWeekdays() {
        return weekdays;
    }

    public List<CouponIssuableTime> getTimeRanges() {
        return timeRanges;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }
}

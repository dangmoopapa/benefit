package im.dangmoo.benefit.domain.data.coupon.policy.issue;

import im.dangmoo.benefit.domain.util.TimeUtils;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public class CouponIssueCondition {

    private CouponIssuablePeriod period;
    private List<CouponIssuableWeekday> weekdays;
    private List<CouponIssuableTime> timeRanges;
    private String segmentId;
    private Long totalQuantity;
    private CouponIssueRepeat repeat;

    private CouponIssueCondition() {
    }

    public static CouponIssueCondition create(
        final CouponIssuablePeriod period,
        final List<CouponIssuableWeekday> weekdays,
        final List<CouponIssuableTime> timeRanges,
        final String segmentId,
        final Long totalQuantity
    ) {
        return create(period, weekdays, timeRanges, segmentId, totalQuantity, CouponIssueRepeat.ONCE);
    }

    public static CouponIssueCondition create(
        final CouponIssuablePeriod period,
        final List<CouponIssuableWeekday> weekdays,
        final List<CouponIssuableTime> timeRanges,
        final String segmentId,
        final Long totalQuantity,
        final CouponIssueRepeat repeat
    ) {
        final CouponIssueCondition entity = new CouponIssueCondition();
        entity.period = period;
        entity.weekdays = weekdays;
        entity.timeRanges = timeRanges;
        entity.segmentId = segmentId;
        entity.totalQuantity = totalQuantity;
        entity.repeat = repeat == null ? CouponIssueRepeat.ONCE : repeat;
        return entity;
    }

    public String idempotencyKey(final String policyId, final String userId, final Instant at) {
        return getRepeat().idempotencyKey(policyId, userId, at);
    }

    public boolean isSatisfiedAt(final Instant now, final Collection<String> userSegmentIds) {
        if (segmentId != null && (userSegmentIds == null || !userSegmentIds.contains(segmentId))) {
            return false;
        }
        if (period.getStart().isAfter(now) || period.getEnd().isBefore(now)) {
            return false;
        }
        if (!weekdays.isEmpty()
            && weekdays.stream().noneMatch(weekday -> weekday.dayOfWeek == TimeUtils.toUtcDayOfWeek(now))) {
            return false;
        }
        if (!timeRanges.isEmpty()) {
            final LocalTime time = TimeUtils.toUtcTime(now);
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

    public CouponIssueRepeat getRepeat() {
        return repeat == null ? CouponIssueRepeat.ONCE : repeat;
    }
}

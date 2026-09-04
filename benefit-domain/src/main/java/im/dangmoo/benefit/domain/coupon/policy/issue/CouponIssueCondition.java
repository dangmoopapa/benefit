package im.dangmoo.benefit.domain.coupon.policy.issue;

import java.util.List;

public class CouponIssueCondition {

    private CouponIssuablePeriod period;
    private List<CouponIssuableWeekday> weekdays;
    private List<CouponIssuableTime> timeRanges;
    private String segmentId;
    private Long totalQuantity;

    protected CouponIssueCondition() {
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

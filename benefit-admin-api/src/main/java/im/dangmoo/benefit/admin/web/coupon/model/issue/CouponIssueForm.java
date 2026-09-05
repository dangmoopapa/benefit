package im.dangmoo.benefit.admin.web.coupon.model.issue;

import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssueCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssuableWeekday;

import java.util.List;

public record CouponIssueForm(
    CouponIssuablePeriodForm period,
    List<CouponIssuableWeekday> weekdays,
    List<CouponIssuableTimeForm> timeRanges,
    String segmentId,
    Long totalQuantity
) {

    public CouponIssueCondition toDocument() {
        return CouponIssueCondition.create(
            period.toDocument(),
            weekdays,
            timeRanges.stream().map(CouponIssuableTimeForm::toDocument).toList(),
            segmentId,
            totalQuantity
        );
    }

    public static CouponIssueForm of(final CouponIssueCondition document) {
        return new CouponIssueForm(
            CouponIssuablePeriodForm.of(document.getPeriod()),
            document.getWeekdays(),
            document.getTimeRanges().stream().map(CouponIssuableTimeForm::of).toList(),
            document.getSegmentId(),
            document.getTotalQuantity()
        );
    }
}

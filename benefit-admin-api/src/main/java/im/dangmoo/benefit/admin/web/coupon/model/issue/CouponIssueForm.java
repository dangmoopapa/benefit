package im.dangmoo.benefit.admin.web.coupon.model.issue;

import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssueCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssuableWeekday;
import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssueRepeat;

import java.util.List;

public record CouponIssueForm(
    CouponIssuablePeriodForm period,
    List<CouponIssuableWeekday> weekdays,
    List<CouponIssuableTimeForm> timeRanges,
    String segmentId,
    Long totalQuantity,
    CouponIssueRepeat repeat
) {

    public CouponIssueCondition toEntity() {
        return CouponIssueCondition.create(
            period.toEntity(),
            weekdays,
            timeRanges.stream().map(CouponIssuableTimeForm::toEntity).toList(),
            segmentId,
            totalQuantity,
            repeat
        );
    }

    public static CouponIssueForm of(final CouponIssueCondition entity) {
        return new CouponIssueForm(
            CouponIssuablePeriodForm.of(entity.getPeriod()),
            entity.getWeekdays(),
            entity.getTimeRanges().stream().map(CouponIssuableTimeForm::of).toList(),
            entity.getSegmentId(),
            entity.getTotalQuantity(),
            entity.getRepeat()
        );
    }
}

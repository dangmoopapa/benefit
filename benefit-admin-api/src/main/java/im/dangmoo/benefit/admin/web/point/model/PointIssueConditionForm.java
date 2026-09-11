package im.dangmoo.benefit.admin.web.point.model;

import im.dangmoo.benefit.domain.data.point.policy.issue.PointIssueCondition;
import im.dangmoo.benefit.domain.data.point.policy.issue.PointIssueRepeat;

public record PointIssueConditionForm(PointIssueRepeat repeat) {

    public PointIssueCondition toEntity() {
        return PointIssueCondition.create(repeat);
    }

    public static PointIssueConditionForm of(final PointIssueCondition entity) {
        if (entity == null) {
            return null;
        }
        return new PointIssueConditionForm(entity.getRepeat());
    }
}

package im.dangmoo.benefit.domain.data.point.policy.issue;

import java.time.Instant;

public class PointIssueCondition {

    private PointIssueRepeat repeat;

    private PointIssueCondition() {
    }

    public static PointIssueCondition create(final PointIssueRepeat repeat) {
        final PointIssueCondition entity = new PointIssueCondition();
        entity.repeat = repeat == null ? PointIssueRepeat.ONCE : repeat;
        return entity;
    }

    public String idempotencyKey(final String policyId, final String userId, final Instant at) {
        return getRepeat().idempotencyKey(policyId, userId, at);
    }

    public PointIssueRepeat getRepeat() {
        return repeat == null ? PointIssueRepeat.ONCE : repeat;
    }
}

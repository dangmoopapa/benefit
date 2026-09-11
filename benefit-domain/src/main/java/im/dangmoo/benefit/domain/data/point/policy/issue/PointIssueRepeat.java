package im.dangmoo.benefit.domain.data.point.policy.issue;

import im.dangmoo.benefit.domain.util.TimeUtils;

import java.time.Instant;

public enum PointIssueRepeat {
    ONCE,
    DAILY,
    MONTHLY,
    YEARLY;

    public String idempotencyKey(final String policyId, final String userId, final Instant at) {
        return switch (this) {
            case ONCE -> policyId + ":" + userId;
            case DAILY -> policyId + ":" + userId + ":" + TimeUtils.toUtcDate(at);
            case MONTHLY -> policyId + ":" + userId + ":" + TimeUtils.toUtcYearMonth(at);
            case YEARLY -> policyId + ":" + userId + ":" + TimeUtils.toUtcYear(at);
        };
    }
}

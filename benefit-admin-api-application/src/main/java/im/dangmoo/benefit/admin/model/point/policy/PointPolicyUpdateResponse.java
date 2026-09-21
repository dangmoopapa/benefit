package im.dangmoo.benefit.admin.model.point.policy;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;

public record PointPolicyUpdateResponse(String id) {

    public static PointPolicyUpdateResponse of(final PointPolicy policy) {
        return new PointPolicyUpdateResponse(policy.getId());
    }
}

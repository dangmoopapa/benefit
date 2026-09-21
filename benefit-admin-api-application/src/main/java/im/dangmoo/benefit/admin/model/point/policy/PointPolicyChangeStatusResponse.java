package im.dangmoo.benefit.admin.model.point.policy;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;

public record PointPolicyChangeStatusResponse(String id) {

    public static PointPolicyChangeStatusResponse of(final PointPolicy policy) {
        return new PointPolicyChangeStatusResponse(policy.getId());
    }
}

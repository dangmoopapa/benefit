package im.dangmoo.benefit.admin.model.point.policy;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;

public record PointPolicyCreateResponse(String id) {

    public static PointPolicyCreateResponse of(final PointPolicy policy) {
        return new PointPolicyCreateResponse(policy.getId());
    }
}

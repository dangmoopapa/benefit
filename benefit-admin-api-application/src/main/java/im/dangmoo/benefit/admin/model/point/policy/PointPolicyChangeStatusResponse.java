package im.dangmoo.benefit.admin.model.point.policy;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyDocument;

public record PointPolicyChangeStatusResponse(String id) {

    public static PointPolicyChangeStatusResponse of(final PointPolicyDocument policy) {
        return new PointPolicyChangeStatusResponse(policy.getId());
    }
}

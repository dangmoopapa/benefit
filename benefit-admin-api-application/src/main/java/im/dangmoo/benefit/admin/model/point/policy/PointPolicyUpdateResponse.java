package im.dangmoo.benefit.admin.model.point.policy;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyDocument;

public record PointPolicyUpdateResponse(String id) {

    public static PointPolicyUpdateResponse of(final PointPolicyDocument policy) {
        return new PointPolicyUpdateResponse(policy.getId());
    }
}

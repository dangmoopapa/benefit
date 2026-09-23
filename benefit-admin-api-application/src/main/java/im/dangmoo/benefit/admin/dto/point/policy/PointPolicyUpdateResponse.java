package im.dangmoo.benefit.admin.dto.point.policy;

import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;

public record PointPolicyUpdateResponse(String id) {

    public static PointPolicyUpdateResponse of(final PointPolicyDocument policy) {
        return new PointPolicyUpdateResponse(policy.getId());
    }
}

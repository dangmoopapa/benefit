package im.dangmoo.benefit.admin.dto.point.policy;

import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;

public record PointPolicyChangeStatusResponse(String id) {

    public static PointPolicyChangeStatusResponse of(final PointPolicyDocument policy) {
        return new PointPolicyChangeStatusResponse(policy.getId());
    }
}

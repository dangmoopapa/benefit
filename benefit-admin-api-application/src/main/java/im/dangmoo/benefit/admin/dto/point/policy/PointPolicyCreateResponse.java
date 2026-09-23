package im.dangmoo.benefit.admin.dto.point.policy;

import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;

public record PointPolicyCreateResponse(String id) {

    public static PointPolicyCreateResponse of(final PointPolicyDocument policy) {
        return new PointPolicyCreateResponse(policy.getId());
    }
}

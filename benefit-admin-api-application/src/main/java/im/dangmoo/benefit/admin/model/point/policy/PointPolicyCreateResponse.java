package im.dangmoo.benefit.admin.model.point.policy;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyDocument;

public record PointPolicyCreateResponse(String id) {

    public static PointPolicyCreateResponse of(final PointPolicyDocument policy) {
        return new PointPolicyCreateResponse(policy.getId());
    }
}

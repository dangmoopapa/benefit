package im.dangmoo.benefit.admin.dto.point.policy;

import im.dangmoo.benefit.data.entity.point.policy.PointPolicyStatus;

public record PointPolicySearchRequest(
    String key,
    String name,
    PointPolicyStatus status
) {
}

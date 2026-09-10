package im.dangmoo.benefit.admin.web.point.model;

import im.dangmoo.benefit.domain.data.point.policy.PointPolicyStatus;

public record PointPolicySearchRequest(
    String code,
    String name,
    String platformId,
    PointPolicyStatus status
) {
}

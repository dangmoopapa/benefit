package im.dangmoo.benefit.admin.model.point.policy;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyStatus;

public record PointPolicyChangeStatusRequest(PointPolicyStatus status) {
}

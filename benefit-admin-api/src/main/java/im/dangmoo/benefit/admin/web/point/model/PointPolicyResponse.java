package im.dangmoo.benefit.admin.web.point.model;

import im.dangmoo.benefit.domain.data.point.policy.PointPolicy;
import im.dangmoo.benefit.domain.data.point.policy.PointPolicyStatus;

import java.time.Instant;

public record PointPolicyResponse(
    String id,
    String code,
    String name,
    String description,
    String platformId,
    PointPolicyStatus status,
    PointIssueConditionForm issueCondition,
    PointExpirationForm expirationCondition,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static PointPolicyResponse of(final PointPolicy entity) {
        return new PointPolicyResponse(
            entity.getId(),
            entity.getCode(),
            entity.getName(),
            entity.getDescription(),
            entity.getPlatformId(),
            entity.getStatus(),
            PointIssueConditionForm.of(entity.getIssueCondition()),
            entity.getExpirationCondition() == null ? null : PointExpirationForm.of(entity.getExpirationCondition()),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedBy(),
            entity.getUpdatedAt()
        );
    }
}

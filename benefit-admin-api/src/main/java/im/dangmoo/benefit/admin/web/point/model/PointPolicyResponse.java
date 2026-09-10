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
    PointExpirationForm expirationCondition,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static PointPolicyResponse of(final PointPolicy document) {
        return new PointPolicyResponse(
            document.getId(),
            document.getCode(),
            document.getName(),
            document.getDescription(),
            document.getPlatformId(),
            document.getStatus(),
            document.getExpirationCondition() == null ? null : PointExpirationForm.of(document.getExpirationCondition()),
            document.getCreatedBy(),
            document.getCreatedAt(),
            document.getUpdatedBy(),
            document.getUpdatedAt()
        );
    }
}

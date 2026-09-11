package im.dangmoo.benefit.admin.web.point.model;

import im.dangmoo.benefit.domain.data.point.policy.PointPolicy;

public record PointPolicyRequest(
    String code,
    String name,
    String description,
    String platformId,
    PointIssueConditionForm issueCondition,
    PointExpirationForm expirationCondition
) {

    public PointPolicy toEntity(final String createdBy) {
        return PointPolicy.create(
            code,
            name,
            description,
            platformId,
            issueCondition == null ? null : issueCondition.toEntity(),
            expirationCondition == null ? null : expirationCondition.toEntity(),
            createdBy
        );
    }
}

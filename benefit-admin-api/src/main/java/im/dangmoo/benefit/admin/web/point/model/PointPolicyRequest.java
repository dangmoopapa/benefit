package im.dangmoo.benefit.admin.web.point.model;

import im.dangmoo.benefit.domain.data.point.policy.PointPolicy;

public record PointPolicyRequest(
    String code,
    String name,
    String description,
    String platformId,
    PointExpirationForm expirationCondition
) {

    public PointPolicy toDocument(final String createdBy) {
        return PointPolicy.create(
            code,
            name,
            description,
            platformId,
            expirationCondition == null ? null : expirationCondition.toDocument(),
            createdBy
        );
    }
}

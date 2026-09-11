package im.dangmoo.benefit.admin.web.membership.model;

import im.dangmoo.benefit.domain.data.membership.policy.MembershipPolicy;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeV1;

import java.time.Instant;

public record MembershipPolicyResponse(
    String id,
    int version,
    String privilege,
    MembershipPeriodForm period,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static MembershipPolicyResponse of(final MembershipPolicy entity) {
        return new MembershipPolicyResponse(
            entity.getId(),
            entity.getVersion(),
            type(entity.getPrivilege()),
            MembershipPeriodForm.of(entity.getPeriod()),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedBy(),
            entity.getUpdatedAt()
        );
    }

    private static String type(final MembershipPrivilege privilege) {
        return switch (privilege) {
            case MembershipPrivilegeV1 _ -> "V1";
            case null -> null;
        };
    }
}

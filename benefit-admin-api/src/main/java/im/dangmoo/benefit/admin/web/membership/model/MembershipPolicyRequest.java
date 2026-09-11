package im.dangmoo.benefit.admin.web.membership.model;

import im.dangmoo.benefit.domain.data.membership.policy.MembershipPolicy;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilege;

public record MembershipPolicyRequest(
    int version,
    MembershipPeriodForm period
) {

    public MembershipPolicy toEntity(final String createdBy) {
        return MembershipPolicy.create(
            version,
            MembershipPrivilege.of(version),
            period == null ? null : period.toEntity(),
            createdBy
        );
    }
}

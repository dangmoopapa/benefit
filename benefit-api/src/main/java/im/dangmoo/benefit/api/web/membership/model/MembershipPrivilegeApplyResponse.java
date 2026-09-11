package im.dangmoo.benefit.api.web.membership.model;

import im.dangmoo.benefit.api.web.point.model.PointTransactionResponse;
import im.dangmoo.benefit.domain.component.point.issue.PointIssue;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeV1;

public record MembershipPrivilegeApplyResponse(
    long point,
    String pointPolicyCode,
    PointTransactionResponse transaction
) {

    public static MembershipPrivilegeApplyResponse skipped() {
        return new MembershipPrivilegeApplyResponse(0L, MembershipPrivilegeV1.POINT_POLICY_CODE, null);
    }

    public static MembershipPrivilegeApplyResponse of(final PointIssue.Success issued) {
        return new MembershipPrivilegeApplyResponse(
            issued.point(),
            MembershipPrivilegeV1.POINT_POLICY_CODE,
            PointTransactionResponse.of(issued)
        );
    }
}

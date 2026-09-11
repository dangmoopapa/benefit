package im.dangmoo.benefit.admin.web.membership.model;

import im.dangmoo.benefit.domain.data.membership.policy.privilege.CachedMembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeV1;
import im.dangmoo.benefit.domain.data.membership.subscription.MembershipSubscription;
import im.dangmoo.benefit.domain.util.TimeUtils;

public record MembershipResponse(
    boolean member,
    String id,
    String userId,
    String policyId,
    Integer version,
    String privilege,
    MembershipPeriodForm period
) {

    public static MembershipResponse none(final String userId) {
        return new MembershipResponse(false, null, userId, null, null, null, null);
    }

    public static MembershipResponse of(
        final MembershipSubscription subscription,
        final CachedMembershipPrivilege cached
    ) {
        return new MembershipResponse(
            subscription.isEffective(TimeUtils.now()),
            subscription.getId(),
            subscription.getUserId(),
            subscription.getPolicyId(),
            cached == null ? null : cached.getVersion(),
            cached == null ? null : type(cached.getPrivilege()),
            MembershipPeriodForm.of(subscription.getPeriod())
        );
    }

    private static String type(final MembershipPrivilege privilege) {
        return switch (privilege) {
            case MembershipPrivilegeV1 _ -> "V1";
            case null -> null;
        };
    }
}

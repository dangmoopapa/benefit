package im.dangmoo.benefit.api.web.membership.model;

import im.dangmoo.benefit.domain.data.membership.policy.MembershipPeriod;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.CachedMembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeV1;
import im.dangmoo.benefit.domain.data.membership.subscription.MembershipSubscription;
import im.dangmoo.benefit.domain.util.TimeUtils;

import java.time.Instant;

public record MembershipResponse(
    boolean member,
    String id,
    String policyId,
    Integer version,
    String privilege,
    MembershipPeriodResponse period
) {

    public static MembershipResponse none() {
        return new MembershipResponse(false, null, null, null, null, null);
    }

    public static MembershipResponse of(
        final MembershipSubscription subscription,
        final CachedMembershipPrivilege cached
    ) {
        return new MembershipResponse(
            subscription.isEffective(TimeUtils.now()),
            subscription.getId(),
            subscription.getPolicyId(),
            cached == null ? null : cached.getVersion(),
            cached == null ? null : type(cached.getPrivilege()),
            MembershipPeriodResponse.of(subscription.getPeriod())
        );
    }

    private static String type(final MembershipPrivilege privilege) {
        return switch (privilege) {
            case MembershipPrivilegeV1 _ -> "V1";
            case null -> null;
        };
    }

    public record MembershipPeriodResponse(Integer months, Instant start, Instant end) {

        static MembershipPeriodResponse of(final MembershipPeriod period) {
            if (period == null) {
                return null;
            }
            return new MembershipPeriodResponse(period.getMonths(), period.getStart(), period.getEnd());
        }
    }
}

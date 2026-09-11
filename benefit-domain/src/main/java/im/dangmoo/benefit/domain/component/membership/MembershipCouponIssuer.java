package im.dangmoo.benefit.domain.component.membership;

import im.dangmoo.benefit.domain.data.membership.policy.privilege.CachedMembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeCacheStore;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeV1;
import im.dangmoo.benefit.domain.data.membership.subscription.MembershipSubscription;
import im.dangmoo.benefit.domain.component.coupon.issue.CouponIssue;
import im.dangmoo.benefit.domain.component.coupon.issue.CouponIssuer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MembershipCouponIssuer {

    private final MembershipPrivilegeCacheStore membershipPrivilegeCacheStore;
    private final CouponIssuer couponIssuer;

    public MembershipCouponIssuer(
        final MembershipPrivilegeCacheStore membershipPrivilegeCacheStore,
        final CouponIssuer couponIssuer
    ) {
        this.membershipPrivilegeCacheStore = membershipPrivilegeCacheStore;
        this.couponIssuer = couponIssuer;
    }

    public CouponIssue issue(final MembershipSubscription subscription, final String actorId) {
        final Optional<CachedMembershipPrivilege> found = membershipPrivilegeCacheStore.get(subscription.getPolicyId());
        if (found.isEmpty() || found.get().getPrivilege() == null) {
            return new CouponIssue.PolicyNotFound();
        }
        return switch (found.get().getPrivilege()) {
            case MembershipPrivilegeV1 _ -> couponIssuer.grantByCode(
                subscription.getUserId(),
                MembershipPrivilegeV1.COUPON_POLICY_CODE,
                actorId
            );
        };
    }
}

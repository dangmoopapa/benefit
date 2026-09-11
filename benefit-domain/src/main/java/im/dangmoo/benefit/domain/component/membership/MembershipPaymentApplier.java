package im.dangmoo.benefit.domain.component.membership;

import im.dangmoo.benefit.domain.data.membership.policy.privilege.CachedMembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeCacheStore;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeV1;
import im.dangmoo.benefit.domain.data.membership.subscription.MembershipSubscription;
import im.dangmoo.benefit.domain.component.point.issue.PointIssue;
import im.dangmoo.benefit.domain.component.point.issue.PointIssuer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MembershipPaymentApplier {

    private final MembershipPrivilegeCacheStore membershipPrivilegeCacheStore;
    private final PointIssuer pointIssuer;

    public MembershipPaymentApplier(
        final MembershipPrivilegeCacheStore membershipPrivilegeCacheStore,
        final PointIssuer pointIssuer
    ) {
        this.membershipPrivilegeCacheStore = membershipPrivilegeCacheStore;
        this.pointIssuer = pointIssuer;
    }

    public Optional<PointIssue> apply(
        final MembershipSubscription subscription,
        final long amount,
        final String orderId,
        final String actorId
    ) {
        final Optional<CachedMembershipPrivilege> found = membershipPrivilegeCacheStore.get(subscription.getPolicyId());
        if (found.isEmpty() || found.get().getPrivilege() == null) {
            return Optional.of(new PointIssue.PolicyNotFound());
        }
        return switch (found.get().getPrivilege()) {
            case MembershipPrivilegeV1 v1 -> {
                final long point = v1.cashbackPoint(amount);
                if (point <= 0) {
                    yield Optional.empty();
                }
                yield Optional.of(pointIssuer.issue(
                    subscription.getUserId(),
                    MembershipPrivilegeV1.POINT_POLICY_CODE,
                    point,
                    orderId,
                    actorId
                ));
            }
        };
    }
}

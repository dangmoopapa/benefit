package im.dangmoo.benefit.admin.web.membership.service;

import im.dangmoo.benefit.admin.support.ApiException;
import im.dangmoo.benefit.admin.support.ApiMessage;
import im.dangmoo.benefit.admin.web.membership.model.MembershipResponse;
import im.dangmoo.benefit.domain.component.membership.MembershipSubscriber;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.CachedMembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeCacheStore;
import im.dangmoo.benefit.domain.data.membership.subscription.MembershipSubscription;
import org.springframework.stereotype.Service;

@Service
public class MembershipSubscriptionService {

    private final MembershipSubscriber membershipSubscriber;
    private final MembershipPrivilegeCacheStore membershipPrivilegeCacheStore;

    public MembershipSubscriptionService(
        final MembershipSubscriber membershipSubscriber,
        final MembershipPrivilegeCacheStore membershipPrivilegeCacheStore
    ) {
        this.membershipSubscriber = membershipSubscriber;
        this.membershipPrivilegeCacheStore = membershipPrivilegeCacheStore;
    }

    public MembershipResponse get(final String userId) {
        return membershipSubscriber.current(userId)
            .map(this::toResponse)
            .orElseGet(() -> MembershipResponse.none(userId));
    }

    public MembershipResponse cancel(final String adminId, final String userId) {
        return membershipSubscriber.cancel(userId, adminId)
            .map(this::toResponse)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_MEMBER));
    }

    private MembershipResponse toResponse(final MembershipSubscription subscription) {
        final CachedMembershipPrivilege cached = membershipPrivilegeCacheStore.get(subscription.getPolicyId())
            .orElse(null);
        return MembershipResponse.of(subscription, cached);
    }
}

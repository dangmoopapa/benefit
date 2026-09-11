package im.dangmoo.benefit.api.web.membership.service;

import im.dangmoo.benefit.api.support.ApiException;
import im.dangmoo.benefit.api.support.ApiMessage;
import im.dangmoo.benefit.api.web.membership.model.MembershipResponse;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.CachedMembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeCacheStore;
import im.dangmoo.benefit.domain.data.membership.subscription.MembershipSubscription;
import im.dangmoo.benefit.domain.component.membership.MembershipSubscriber;
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
            .orElseGet(MembershipResponse::none);
    }

    public MembershipResponse join(final String userId) {
        if (membershipSubscriber.current(userId).isPresent()) {
            throw new ApiException(ApiMessage.ALREADY_MEMBER);
        }
        return membershipSubscriber.join(userId)
            .map(this::toResponse)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
    }

    public MembershipResponse cancel(final String userId) {
        return membershipSubscriber.cancel(userId, userId)
            .map(this::toResponse)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_MEMBER));
    }

    private MembershipResponse toResponse(final MembershipSubscription subscription) {
        final CachedMembershipPrivilege cached = membershipPrivilegeCacheStore.get(subscription.getPolicyId())
            .orElse(null);
        return MembershipResponse.of(subscription, cached);
    }
}

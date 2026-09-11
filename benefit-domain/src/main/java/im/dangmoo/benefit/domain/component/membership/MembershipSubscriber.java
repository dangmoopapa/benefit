package im.dangmoo.benefit.domain.component.membership;

import im.dangmoo.benefit.domain.data.membership.policy.MembershipPeriod;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.CachedMembershipPrivilege;
import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilegeCacheStore;
import im.dangmoo.benefit.domain.data.membership.subscription.MembershipSubscription;
import im.dangmoo.benefit.domain.data.membership.subscription.MembershipSubscriptionRepository;
import im.dangmoo.benefit.domain.util.TimeUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class MembershipSubscriber {

    private final MembershipSubscriptionRepository membershipSubscriptionRepository;
    private final MembershipPrivilegeCacheStore membershipPrivilegeCacheStore;

    public MembershipSubscriber(
        final MembershipSubscriptionRepository membershipSubscriptionRepository,
        final MembershipPrivilegeCacheStore membershipPrivilegeCacheStore
    ) {
        this.membershipSubscriptionRepository = membershipSubscriptionRepository;
        this.membershipPrivilegeCacheStore = membershipPrivilegeCacheStore;
    }

    public Optional<MembershipSubscription> current(final String userId) {
        final Optional<MembershipSubscription> found = membershipSubscriptionRepository.findByUserId(userId);
        if (found.isEmpty() || !found.get().isEffective(TimeUtils.now())) {
            return Optional.empty();
        }
        return found;
    }

    public Optional<MembershipSubscription> join(final String userId) {
        final Optional<MembershipSubscription> current = current(userId);
        if (current.isPresent()) {
            return current;
        }
        final Optional<CachedMembershipPrivilege> found = membershipPrivilegeCacheStore.latest();
        if (found.isEmpty()) {
            return Optional.empty();
        }
        final CachedMembershipPrivilege cached = found.get();
        final Instant now = TimeUtils.now();
        final MembershipPeriod period = cached.getPeriod() == null ? null : cached.getPeriod().open(now);
        final Optional<MembershipSubscription> existing = membershipSubscriptionRepository.findByUserId(userId);
        return existing.map(membershipSubscription -> membershipSubscriptionRepository.save(
            membershipSubscription.renew(cached.getPolicyId(), period)
        )).or(() -> Optional.of(membershipSubscriptionRepository.save(MembershipSubscription.join(
            userId,
            cached.getPolicyId(),
            period
        ))));
    }

    public Optional<MembershipSubscription> cancel(final String userId, final String actorId) {
        final Optional<MembershipSubscription> found = current(userId);
        return found.map(membershipSubscription -> membershipSubscriptionRepository.save(
            membershipSubscription.cancel(TimeUtils.now(), actorId)
        ));
    }
}

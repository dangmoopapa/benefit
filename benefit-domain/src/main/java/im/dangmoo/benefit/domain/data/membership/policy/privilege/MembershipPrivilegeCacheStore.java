package im.dangmoo.benefit.domain.data.membership.policy.privilege;

import im.dangmoo.benefit.domain.data.membership.policy.MembershipPolicy;
import im.dangmoo.benefit.domain.infrastructure.cache.CacheType;
import im.dangmoo.benefit.domain.data.membership.policy.MembershipPolicyRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MembershipPrivilegeCacheStore {

    private static final String LATEST = "latest";

    private final Cache cache;
    private final MembershipPolicyRepository membershipPolicyRepository;

    public MembershipPrivilegeCacheStore(
        final CacheManager cacheManager,
        final MembershipPolicyRepository membershipPolicyRepository
    ) {
        this.cache = cacheManager.getCache(CacheType.MEMBERSHIP_POLICY.cacheName());
        this.membershipPolicyRepository = membershipPolicyRepository;
    }

    public Optional<CachedMembershipPrivilege> get(final String policyId) {
        final CachedMembershipPrivilege cached = cache.get(policyId, CachedMembershipPrivilege.class);
        if (cached != null) {
            return Optional.of(cached);
        }
        return membershipPolicyRepository.findById(policyId).map(this::put);
    }

    public Optional<CachedMembershipPrivilege> latest() {
        final CachedMembershipPrivilege cached = cache.get(LATEST, CachedMembershipPrivilege.class);
        if (cached != null) {
            return Optional.of(cached);
        }
        return membershipPolicyRepository.findLatest().map(policy -> {
            final CachedMembershipPrivilege value = put(policy);
            cache.put(LATEST, value);
            return value;
        });
    }

    public CachedMembershipPrivilege put(final MembershipPolicy policy) {
        final CachedMembershipPrivilege cached = CachedMembershipPrivilege.of(policy);
        cache.put(policy.getId(), cached);
        cache.evict(LATEST);
        return cached;
    }
}

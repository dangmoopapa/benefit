package im.dangmoo.benefit.data.infrastructure.coupon;

import im.dangmoo.benefit.data.infrastructure.CacheKeys;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import im.dangmoo.benefit.data.entity.coupon.policy.*;

@Component
public class CouponPolicyCacheRepository {

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;

    public CouponPolicyCacheRepository(final CouponPolicyMongoRepository couponPolicyMongoRepository) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
    }

    @Cacheable(cacheNames = CacheKeys.COUPON_POLICY, key = "#key")
    public CouponPolicyCache findByKey(final String key) {
        return couponPolicyMongoRepository.findByKey(key)
            .map(CouponPolicyCache::of)
            .orElse(null);
    }

    @CachePut(cacheNames = CacheKeys.COUPON_POLICY, key = "#result.key")
    public CouponPolicyCache put(final CouponPolicyDocument policy) {
        return CouponPolicyCache.of(policy);
    }
}

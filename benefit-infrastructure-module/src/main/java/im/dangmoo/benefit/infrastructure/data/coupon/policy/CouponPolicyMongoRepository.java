package im.dangmoo.benefit.infrastructure.data.coupon.policy;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CouponPolicyMongoRepository {

    private final MongoTemplate mongoTemplate;

    public CouponPolicyMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public CouponPolicy save(final CouponPolicy policy) {
        return mongoTemplate.save(policy);
    }

    public Optional<CouponPolicy> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, CouponPolicy.class));
    }

    public boolean existsByKey(final String key) {
        return mongoTemplate.exists(CouponPolicy.queryByKey(key), CouponPolicy.class);
    }

    public Optional<CouponPolicy> findByKey(final String key) {
        return Optional.ofNullable(mongoTemplate.findOne(CouponPolicy.queryByKey(key), CouponPolicy.class));
    }

    public List<CouponPolicy> search(
        final String key,
        final String name,
        final CouponPolicyType type,
        final CouponPolicyStatus status
    ) {
        return mongoTemplate.find(CouponPolicy.query(key, name, type, status), CouponPolicy.class);
    }

    public List<CouponPolicy> findActiveVouchers(final String productId, final String brandId) {
        return mongoTemplate.find(CouponPolicy.queryActiveVouchers(productId, brandId), CouponPolicy.class);
    }

    public List<CouponPolicy> findVouchers(
        final String productId,
        final String brandId,
        final CouponPolicyStatus status
    ) {
        return mongoTemplate.find(CouponPolicy.queryVouchers(productId, brandId, status), CouponPolicy.class);
    }
}

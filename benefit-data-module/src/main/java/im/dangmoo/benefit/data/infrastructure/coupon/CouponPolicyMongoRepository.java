package im.dangmoo.benefit.data.infrastructure.coupon;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import im.dangmoo.benefit.data.entity.coupon.policy.*;

@Repository
public class CouponPolicyMongoRepository {

    private final MongoTemplate mongoTemplate;

    public CouponPolicyMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public CouponPolicyDocument save(final CouponPolicyDocument policy) {
        return mongoTemplate.save(policy);
    }

    public Optional<CouponPolicyDocument> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, CouponPolicyDocument.class));
    }

    public boolean existsByKey(final String key) {
        return mongoTemplate.exists(CouponPolicyDocument.queryByKey(key), CouponPolicyDocument.class);
    }

    public Optional<CouponPolicyDocument> findByKey(final String key) {
        return Optional.ofNullable(mongoTemplate.findOne(CouponPolicyDocument.queryByKey(key), CouponPolicyDocument.class));
    }

    public List<CouponPolicyDocument> search(
        final String key,
        final String name,
        final CouponPolicyType type,
        final CouponPolicyStatus status
    ) {
        return mongoTemplate.find(CouponPolicyDocument.query(key, name, type, status), CouponPolicyDocument.class);
    }

    public List<CouponPolicyDocument> findActiveVouchers(final String productId, final String brandId) {
        return mongoTemplate.find(CouponPolicyDocument.queryActiveVouchers(productId, brandId), CouponPolicyDocument.class);
    }

    public List<CouponPolicyDocument> findVouchers(
        final String productId,
        final String brandId,
        final CouponPolicyStatus status
    ) {
        return mongoTemplate.find(CouponPolicyDocument.queryVouchers(productId, brandId, status), CouponPolicyDocument.class);
    }
}

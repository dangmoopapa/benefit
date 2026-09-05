package im.dangmoo.benefit.domain.data.coupon.policy;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CouponPolicyRepository {

    private final MongoTemplate mongoTemplate;

    public CouponPolicyRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CouponPolicy> findAll(
        final String code,
        final String name,
        final String platformId,
        final CouponPolicyType type,
        final CouponPolicyStatus status
    ) {
        return mongoTemplate.find(CouponPolicy.query(code, name, platformId, type, status), CouponPolicy.class);
    }

    public Optional<CouponPolicy> findById(final String policyId) {
        return Optional.ofNullable(mongoTemplate.findById(policyId, CouponPolicy.class));
    }

    public boolean existsByCode(final String code) {
        return mongoTemplate.exists(CouponPolicy.queryByCode(code), CouponPolicy.class);
    }

    public CouponPolicy save(final CouponPolicy policy) {
        return mongoTemplate.save(policy);
    }
}

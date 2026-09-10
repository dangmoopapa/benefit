package im.dangmoo.benefit.domain.data.point.policy;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PointPolicyRepository {

    private final MongoTemplate mongoTemplate;

    public PointPolicyRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<PointPolicy> findAll(
        final String code,
        final String name,
        final String platformId,
        final PointPolicyStatus status
    ) {
        return mongoTemplate.find(PointPolicy.query(code, name, platformId, status), PointPolicy.class);
    }

    public Optional<PointPolicy> findById(final String policyId) {
        return Optional.ofNullable(mongoTemplate.findById(policyId, PointPolicy.class));
    }

    public Optional<PointPolicy> findByCode(final String code) {
        return Optional.ofNullable(mongoTemplate.findOne(PointPolicy.queryByCode(code), PointPolicy.class));
    }

    public boolean existsByCode(final String code) {
        return mongoTemplate.exists(PointPolicy.queryByCode(code), PointPolicy.class);
    }

    public PointPolicy save(final PointPolicy policy) {
        return mongoTemplate.save(policy);
    }
}

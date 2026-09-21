package im.dangmoo.benefit.infrastructure.data.point.policy;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PointPolicyMongoRepository {

    private final MongoTemplate mongoTemplate;

    public PointPolicyMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PointPolicy save(final PointPolicy policy) {
        return mongoTemplate.save(policy);
    }

    public Optional<PointPolicy> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, PointPolicy.class));
    }

    public Optional<PointPolicy> findByKey(final String key) {
        return Optional.ofNullable(mongoTemplate.findOne(PointPolicy.queryByKey(key), PointPolicy.class));
    }

    public boolean existsByKey(final String key) {
        return mongoTemplate.exists(PointPolicy.queryByKey(key), PointPolicy.class);
    }

    public List<PointPolicy> search(final String key, final String name, final PointPolicyStatus status) {
        return mongoTemplate.find(PointPolicy.query(key, name, status), PointPolicy.class);
    }
}

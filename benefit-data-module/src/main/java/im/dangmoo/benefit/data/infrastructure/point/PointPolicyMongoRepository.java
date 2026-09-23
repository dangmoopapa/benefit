package im.dangmoo.benefit.data.infrastructure.point;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import im.dangmoo.benefit.data.entity.point.policy.*;

@Repository
public class PointPolicyMongoRepository {

    private final MongoTemplate mongoTemplate;

    public PointPolicyMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PointPolicyDocument save(final PointPolicyDocument policy) {
        return mongoTemplate.save(policy);
    }

    public Optional<PointPolicyDocument> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, PointPolicyDocument.class));
    }

    public Optional<PointPolicyDocument> findByKey(final String key) {
        return Optional.ofNullable(mongoTemplate.findOne(PointPolicyDocument.queryByKey(key), PointPolicyDocument.class));
    }

    public boolean existsByKey(final String key) {
        return mongoTemplate.exists(PointPolicyDocument.queryByKey(key), PointPolicyDocument.class);
    }

    public List<PointPolicyDocument> search(final String key, final String name, final PointPolicyStatus status) {
        return mongoTemplate.find(PointPolicyDocument.query(key, name, status), PointPolicyDocument.class);
    }
}

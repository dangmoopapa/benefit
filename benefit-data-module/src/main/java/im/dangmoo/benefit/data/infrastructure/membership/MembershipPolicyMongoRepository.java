package im.dangmoo.benefit.data.infrastructure.membership;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import im.dangmoo.benefit.data.entity.membership.policy.*;

@Repository
public class MembershipPolicyMongoRepository {

    private final MongoTemplate mongoTemplate;

    public MembershipPolicyMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MembershipPolicyDocument save(final MembershipPolicyDocument policy) {
        return mongoTemplate.save(policy);
    }

    public Optional<MembershipPolicyDocument> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, MembershipPolicyDocument.class));
    }

    public Optional<MembershipPolicyDocument> findByKey(final String key) {
        return Optional.ofNullable(
            mongoTemplate.findOne(MembershipPolicyDocument.queryByKey(key), MembershipPolicyDocument.class)
        );
    }

    public boolean existsByKey(final String key) {
        return mongoTemplate.exists(MembershipPolicyDocument.queryByKey(key), MembershipPolicyDocument.class);
    }

    public List<MembershipPolicyDocument> search(
        final String key,
        final String name,
        final MembershipPolicyStatus status,
        final MembershipSeason season
    ) {
        return mongoTemplate.find(
            MembershipPolicyDocument.query(key, name, status, season),
            MembershipPolicyDocument.class
        );
    }
}

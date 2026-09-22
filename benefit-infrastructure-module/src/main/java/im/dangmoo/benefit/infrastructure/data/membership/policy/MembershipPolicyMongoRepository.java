package im.dangmoo.benefit.infrastructure.data.membership.policy;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MembershipPolicyMongoRepository {

    private final MongoTemplate mongoTemplate;

    public MembershipPolicyMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MembershipPolicy save(final MembershipPolicy policy) {
        return mongoTemplate.save(policy);
    }

    public Optional<MembershipPolicy> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, MembershipPolicy.class));
    }

    public Optional<MembershipPolicy> findByKey(final String key) {
        return Optional.ofNullable(
            mongoTemplate.findOne(MembershipPolicy.queryByKey(key), MembershipPolicy.class)
        );
    }

    public boolean existsByKey(final String key) {
        return mongoTemplate.exists(MembershipPolicy.queryByKey(key), MembershipPolicy.class);
    }

    public List<MembershipPolicy> search(
        final String key,
        final String name,
        final MembershipPolicyStatus status,
        final MembershipSeason season
    ) {
        return mongoTemplate.find(
            MembershipPolicy.query(key, name, status, season),
            MembershipPolicy.class
        );
    }
}

package im.dangmoo.benefit.domain.data.membership.policy;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MembershipPolicyRepository {

    private final MongoTemplate mongoTemplate;

    public MembershipPolicyRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<MembershipPolicy> findAll(final Integer version) {
        return mongoTemplate.find(MembershipPolicy.query(version), MembershipPolicy.class);
    }

    public Optional<MembershipPolicy> findById(final String policyId) {
        return Optional.ofNullable(mongoTemplate.findById(policyId, MembershipPolicy.class));
    }

    public Optional<MembershipPolicy> findLatest() {
        return Optional.ofNullable(mongoTemplate.findOne(MembershipPolicy.queryLatest(), MembershipPolicy.class));
    }

    public boolean existsByVersion(final int version) {
        return mongoTemplate.exists(MembershipPolicy.queryByVersion(version), MembershipPolicy.class);
    }

    public MembershipPolicy save(final MembershipPolicy policy) {
        return mongoTemplate.save(policy);
    }
}

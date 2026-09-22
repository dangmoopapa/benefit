package im.dangmoo.benefit.infrastructure.data.membership.contract;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class MembershipContractMongoRepository {

    private final MongoTemplate mongoTemplate;

    public MembershipContractMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MembershipContract save(final MembershipContract contract) {
        return mongoTemplate.save(contract);
    }

    public Optional<MembershipContract> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, MembershipContract.class));
    }

    public Optional<MembershipContract> findByIdempotencyKey(final String idempotencyKey) {
        return Optional.ofNullable(
            mongoTemplate.findOne(
                MembershipContract.queryByIdempotencyKey(idempotencyKey),
                MembershipContract.class
            )
        );
    }

    public Optional<MembershipContract> findEffectiveByUserId(final String userId, final Instant now) {
        return Optional.ofNullable(
            mongoTemplate.findOne(
                MembershipContract.queryEffectiveByUserId(userId, now),
                MembershipContract.class
            )
        );
    }

    public List<MembershipContract> search(
        final String userId,
        final String policyId,
        final MembershipContractStatus status
    ) {
        return mongoTemplate.find(
            MembershipContract.query(userId, policyId, status),
            MembershipContract.class
        );
    }
}

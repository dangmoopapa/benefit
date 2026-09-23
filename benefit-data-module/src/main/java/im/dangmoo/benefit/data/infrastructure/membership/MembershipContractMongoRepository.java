package im.dangmoo.benefit.data.infrastructure.membership;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import im.dangmoo.benefit.data.entity.membership.contract.*;

@Repository
public class MembershipContractMongoRepository {

    private final MongoTemplate mongoTemplate;

    public MembershipContractMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MembershipContractDocument save(final MembershipContractDocument contract) {
        return mongoTemplate.save(contract);
    }

    public Optional<MembershipContractDocument> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, MembershipContractDocument.class));
    }

    public Optional<MembershipContractDocument> findByIdempotencyKey(final String idempotencyKey) {
        return Optional.ofNullable(
            mongoTemplate.findOne(
                MembershipContractDocument.queryByIdempotencyKey(idempotencyKey),
                MembershipContractDocument.class
            )
        );
    }

    public Optional<MembershipContractDocument> findEffectiveByUserId(final String userId, final Instant now) {
        return Optional.ofNullable(
            mongoTemplate.findOne(
                MembershipContractDocument.queryEffectiveByUserId(userId, now),
                MembershipContractDocument.class
            )
        );
    }

    public List<MembershipContractDocument> search(
        final String userId,
        final String policyId,
        final MembershipContractStatus status
    ) {
        return mongoTemplate.find(
            MembershipContractDocument.query(userId, policyId, status),
            MembershipContractDocument.class
        );
    }
}

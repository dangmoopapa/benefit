package im.dangmoo.benefit.infrastructure.data.membership.history;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MembershipBenefitHistoryMongoRepository {

    private final MongoTemplate mongoTemplate;

    public MembershipBenefitHistoryMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MembershipBenefitHistoryDocument save(final MembershipBenefitHistoryDocument history) {
        return mongoTemplate.save(history);
    }

    public Optional<MembershipBenefitHistoryDocument> findByOrderId(final String orderId) {
        return Optional.ofNullable(
            mongoTemplate.findOne(
                MembershipBenefitHistoryDocument.queryByOrderId(orderId),
                MembershipBenefitHistoryDocument.class
            )
        );
    }

    public List<MembershipBenefitHistoryDocument> search(
        final String userId,
        final String contractId,
        final int page,
        final int size
    ) {
        final Query query = MembershipBenefitHistoryDocument.query(userId, contractId)
            .with(Sort.by(Sort.Direction.DESC, MembershipBenefitHistoryDocument.TRANSACTION_AT));
        query.skip((long) page * size).limit(size);
        return mongoTemplate.find(query, MembershipBenefitHistoryDocument.class);
    }

    public long count(final String userId, final String contractId) {
        return mongoTemplate.count(
            MembershipBenefitHistoryDocument.query(userId, contractId),
            MembershipBenefitHistoryDocument.class
        );
    }
}

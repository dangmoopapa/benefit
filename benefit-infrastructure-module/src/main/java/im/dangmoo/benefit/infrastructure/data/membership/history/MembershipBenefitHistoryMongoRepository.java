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

    public MembershipBenefitHistory save(final MembershipBenefitHistory history) {
        return mongoTemplate.save(history);
    }

    public Optional<MembershipBenefitHistory> findByOrderId(final String orderId) {
        return Optional.ofNullable(
            mongoTemplate.findOne(
                MembershipBenefitHistory.queryByOrderId(orderId),
                MembershipBenefitHistory.class
            )
        );
    }

    public List<MembershipBenefitHistory> search(
        final String userId,
        final String contractId,
        final int page,
        final int size
    ) {
        final Query query = MembershipBenefitHistory.query(userId, contractId)
            .with(Sort.by(Sort.Direction.DESC, MembershipBenefitHistory.TRANSACTION_AT));
        query.skip((long) page * size).limit(size);
        return mongoTemplate.find(query, MembershipBenefitHistory.class);
    }

    public long count(final String userId, final String contractId) {
        return mongoTemplate.count(
            MembershipBenefitHistory.query(userId, contractId),
            MembershipBenefitHistory.class
        );
    }
}

package im.dangmoo.benefit.domain.data.point.transaction;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class PointTransactionRepository {

    private final MongoTemplate mongoTemplate;

    public PointTransactionRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<PointTransaction> findById(final String transactionId) {
        return Optional.ofNullable(mongoTemplate.findById(transactionId, PointTransaction.class));
    }

    public Optional<PointTransaction> findByIdempotencyKey(final String idempotencyKey) {
        return Optional.ofNullable(
            mongoTemplate.findOne(PointTransaction.queryByIdempotencyKey(idempotencyKey), PointTransaction.class)
        );
    }

    public boolean existsByRelatedTransactionId(final String relatedTransactionId) {
        return mongoTemplate.exists(
            PointTransaction.queryByRelatedTransactionId(relatedTransactionId),
            PointTransaction.class
        );
    }

    public List<PointTransaction> findByUserId(
        final String userId,
        final Collection<PointTransactionType> types
    ) {
        return mongoTemplate.find(
            PointTransaction.queryByUserId(userId, types)
                .with(Sort.by(Sort.Direction.DESC, PointTransaction.transactionAtField())),
            PointTransaction.class
        );
    }

    public Optional<PointTransaction> tryInsert(final PointTransaction transaction) {
        try {
            return Optional.of(mongoTemplate.insert(transaction));
        } catch (final DuplicateKeyException ignored) {
            return Optional.empty();
        }
    }
}

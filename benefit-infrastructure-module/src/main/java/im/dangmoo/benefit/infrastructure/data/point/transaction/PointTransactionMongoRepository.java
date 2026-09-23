package im.dangmoo.benefit.infrastructure.data.point.transaction;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class PointTransactionMongoRepository {

    public record Appended(PointTransactionDocument tx, boolean created) {
    }

    private final MongoTemplate mongoTemplate;

    public PointTransactionMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Appended append(final PointTransactionDocument transaction) {
        try {
            return new Appended(mongoTemplate.insert(transaction), true);
        } catch (final DuplicateKeyException ignored) {
            return new Appended(
                findByIdempotencyKey(transaction.getIdempotencyKey()).orElseThrow(),
                false
            );
        }
    }

    public Optional<PointTransactionDocument> findByIdempotencyKey(final String idempotencyKey) {
        return Optional.ofNullable(
            mongoTemplate.findOne(PointTransactionDocument.queryByIdempotencyKey(idempotencyKey), PointTransactionDocument.class)
        );
    }

    public List<PointTransactionDocument> search(final String userId, final String policyId, final int page, final int size) {
        final Query query = PointTransactionDocument.query(userId, policyId);
        query.skip((long) page * size).limit(size);
        return mongoTemplate.find(query, PointTransactionDocument.class);
    }

    public long count(final String userId, final String policyId) {
        return mongoTemplate.count(PointTransactionDocument.query(userId, policyId), PointTransactionDocument.class);
    }

    public List<PointTransactionDocument> findByUserIdAndTypes(
        final String userId,
        final Collection<PointTransactionType> types,
        final int page,
        final int size
    ) {
        final Query query = PointTransactionDocument.queryByUserIdAndTypes(userId, types);
        query.skip((long) page * size).limit(size);
        return mongoTemplate.find(query, PointTransactionDocument.class);
    }

    public long countByUserIdAndTypes(
        final String userId,
        final Collection<PointTransactionType> types
    ) {
        return mongoTemplate.count(PointTransactionDocument.queryByUserIdAndTypes(userId, types), PointTransactionDocument.class);
    }
}

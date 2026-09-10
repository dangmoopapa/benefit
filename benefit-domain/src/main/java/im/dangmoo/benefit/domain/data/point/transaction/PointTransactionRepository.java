package im.dangmoo.benefit.domain.data.point.transaction;

import im.dangmoo.benefit.domain.infrastructure.mongo.DocumentSlice;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    /**
     * 커서(keyset) 페이징. count/skip 없음.
     * size+1 조회 후 hasNext 판단. 인덱스: userId + transactionAt + _id.
     */
    public DocumentSlice<PointTransaction> findByUserId(
        final String userId,
        final Collection<PointTransactionType> types,
        final String cursor,
        final int size
    ) {
        final PointTransactionCursor.Decoded decoded = PointTransactionCursor.decode(cursor);
        final var query = PointTransaction.queryByUserId(
                userId,
                types,
                decoded == null ? null : decoded.transactionAt(),
                decoded == null ? null : decoded.id()
            )
            .with(Sort.by(
                Sort.Order.desc(PointTransaction.transactionAtField()),
                Sort.Order.desc(PointTransaction.idField())
            ))
            .limit(size + 1);

        final List<PointTransaction> fetched = mongoTemplate.find(query, PointTransaction.class);
        final boolean hasNext = fetched.size() > size;
        final List<PointTransaction> content = hasNext
            ? new ArrayList<>(fetched.subList(0, size))
            : fetched;

        final String nextCursor;
        if (hasNext && !content.isEmpty()) {
            final PointTransaction last = content.getLast();
            nextCursor = PointTransactionCursor.encode(last.getTransactionAt(), last.getId());
        } else {
            nextCursor = null;
        }

        return new DocumentSlice<>(content, size, hasNext, nextCursor);
    }

    public Optional<PointTransaction> tryInsert(final PointTransaction transaction) {
        try {
            return Optional.of(mongoTemplate.insert(transaction));
        } catch (final DuplicateKeyException ignored) {
            return Optional.empty();
        }
    }
}

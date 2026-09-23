package im.dangmoo.benefit.data.infrastructure.point;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import im.dangmoo.benefit.data.entity.point.balance.*;

@Repository
public class PointBalanceMongoRepository {

    private final MongoTemplate mongoTemplate;

    public PointBalanceMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PointBalanceDocument save(final PointBalanceDocument balance) {
        return mongoTemplate.save(balance);
    }

    public Optional<PointBalanceDocument> findByUserId(final String userId) {
        return Optional.ofNullable(
            mongoTemplate.findOne(PointBalanceDocument.queryByUserId(userId), PointBalanceDocument.class)
        );
    }

    public List<PointBalanceDocument> findDueForExpire(final Instant asOf, final int limit) {
        final Query query = PointBalanceDocument.queryDueForExpire(asOf);
        query.limit(limit);
        return mongoTemplate.find(query, PointBalanceDocument.class);
    }

    public void increase(final String userId, final Instant expiresAt, final long amount) {
        for (int attempt = 0; true; attempt++) {
            final PointBalanceDocument balance = findByUserId(userId).orElseGet(() -> PointBalanceDocument.create(userId));
            try {
                save(balance.increase(expiresAt, amount));
                return;
            } catch (final OptimisticLockingFailureException ex) {
                if (attempt == 2) {
                    throw ex;
                }
            }
        }
    }

    public void consume(final String userId, final long amount, final Instant now) {
        for (int attempt = 0; true; attempt++) {
            final PointBalanceDocument balance = findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("insufficient point"));
            try {
                balance.decreaseByExpiresAt(amount, now);
                save(balance);
                return;
            } catch (final OptimisticLockingFailureException ex) {
                if (attempt == 2) {
                    throw ex;
                }
            }
        }
    }
}

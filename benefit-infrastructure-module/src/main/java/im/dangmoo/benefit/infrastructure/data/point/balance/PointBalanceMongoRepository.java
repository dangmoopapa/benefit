package im.dangmoo.benefit.infrastructure.data.point.balance;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class PointBalanceMongoRepository {

    private final MongoTemplate mongoTemplate;

    public PointBalanceMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PointBalance save(final PointBalance balance) {
        return mongoTemplate.save(balance);
    }

    public Optional<PointBalance> findByUserId(final String userId) {
        return Optional.ofNullable(
            mongoTemplate.findOne(PointBalance.queryByUserId(userId), PointBalance.class)
        );
    }

    public List<PointBalance> findDueForExpire(final Instant asOf, final int limit) {
        final Query query = PointBalance.queryDueForExpire(asOf);
        query.limit(limit);
        return mongoTemplate.find(query, PointBalance.class);
    }

    public void increase(final String userId, final Instant expiresAt, final long amount) {
        for (int attempt = 0; true; attempt++) {
            final PointBalance balance = findByUserId(userId).orElseGet(() -> PointBalance.create(userId));
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
            final PointBalance balance = findByUserId(userId)
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

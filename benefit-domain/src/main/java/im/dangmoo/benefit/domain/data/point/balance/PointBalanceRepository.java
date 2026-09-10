package im.dangmoo.benefit.domain.data.point.balance;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class PointBalanceRepository {

    private static final int MAX_RETRY = 3;

    private final MongoTemplate mongoTemplate;

    public PointBalanceRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<PointBalance> findByUserId(final String userId) {
        return Optional.ofNullable(
            mongoTemplate.findOne(PointBalance.queryByUserId(userId), PointBalance.class)
        );
    }

    public PointBalance getOrCreate(final String userId, final String actorId, final Instant now) {
        return findByUserId(userId).orElseGet(() -> {
            try {
                return mongoTemplate.insert(PointBalance.create(userId, actorId, now));
            } catch (final org.springframework.dao.DuplicateKeyException ignored) {
                return findByUserId(userId).orElseThrow();
            }
        });
    }

    public PointBalance save(final PointBalance balance) {
        return mongoTemplate.save(balance);
    }

    public PointBalance issue(
        final String userId,
        final long amount,
        final Instant expiresAt,
        final String actorId,
        final Instant now
    ) {
        OptimisticLockingFailureException last = null;
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            final PointBalance balance = getOrCreate(userId, actorId, now);
            balance.issue(amount, expiresAt, actorId, now);
            try {
                return save(balance);
            } catch (final OptimisticLockingFailureException ex) {
                last = ex;
            }
        }
        throw last;
    }

    public PointBalance revoke(
        final String userId,
        final long amount,
        final Instant expiresAt,
        final String actorId,
        final Instant now
    ) {
        OptimisticLockingFailureException last = null;
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            final PointBalance balance = getOrCreate(userId, actorId, now);
            balance.revoke(amount, expiresAt, actorId, now);
            try {
                return save(balance);
            } catch (final OptimisticLockingFailureException ex) {
                last = ex;
            }
        }
        throw last;
    }

    public List<PointLot> spend(final String userId, final long amount, final String actorId, final Instant now) {
        OptimisticLockingFailureException last = null;
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            final PointBalance balance = getOrCreate(userId, actorId, now);
            final List<PointLot> usedLots = balance.spend(amount, actorId, now);
            try {
                save(balance);
                return usedLots;
            } catch (final OptimisticLockingFailureException ex) {
                last = ex;
            }
        }
        throw last;
    }

    public PointBalance restore(
        final String userId,
        final List<PointLot> usedLots,
        final String actorId,
        final Instant now
    ) {
        OptimisticLockingFailureException last = null;
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            final PointBalance balance = getOrCreate(userId, actorId, now);
            balance.restore(usedLots, actorId, now);
            try {
                return save(balance);
            } catch (final OptimisticLockingFailureException ex) {
                last = ex;
            }
        }
        throw last;
    }
}

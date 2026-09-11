package im.dangmoo.benefit.domain.data.point.balance;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class PointBalanceRepository {

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
}

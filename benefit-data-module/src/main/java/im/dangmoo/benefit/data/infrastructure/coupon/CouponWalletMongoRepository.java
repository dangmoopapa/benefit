package im.dangmoo.benefit.data.infrastructure.coupon;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import im.dangmoo.benefit.data.entity.coupon.wallet.*;

@Repository
public class CouponWalletMongoRepository {

    private final MongoTemplate mongoTemplate;

    public CouponWalletMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public CouponWalletDocument save(final CouponWalletDocument wallet) {
        return mongoTemplate.save(wallet);
    }

    public void insert(final CouponWalletDocument wallet) {
        try {
            mongoTemplate.insert(wallet);
        } catch (final DuplicateKeyException ignored) {
        }
    }

    public Optional<CouponWalletDocument> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, CouponWalletDocument.class));
    }

    public Optional<CouponWalletDocument> findByIdempotencyKey(final String idempotencyKey) {
        return Optional.ofNullable(
            mongoTemplate.findOne(CouponWalletDocument.queryByIdempotencyKey(idempotencyKey), CouponWalletDocument.class)
        );
    }

    public Set<String> findExistingIdempotencyKeys(final Collection<String> idempotencyKeys) {
        if (idempotencyKeys == null || idempotencyKeys.isEmpty()) {
            return Set.of();
        }
        final Query query = Query.query(Criteria.where(CouponWalletDocument.IDEMPOTENCY_KEY).in(idempotencyKeys));
        query.fields().include(CouponWalletDocument.IDEMPOTENCY_KEY);
        return mongoTemplate.find(query, CouponWalletDocument.class).stream()
            .map(CouponWalletDocument::getIdempotencyKey)
            .collect(Collectors.toCollection(HashSet::new));
    }

    public List<CouponWalletDocument> findByUserId(final String userId) {
        return mongoTemplate.find(CouponWalletDocument.queryByUserId(userId), CouponWalletDocument.class);
    }

    public List<CouponWalletDocument> findByUserIdAndStatus(final String userId, final CouponWalletStatus status) {
        return mongoTemplate.find(CouponWalletDocument.queryByUserIdAndStatus(userId, status), CouponWalletDocument.class);
    }

    public List<CouponWalletDocument> search(
        final String userId,
        final String orderId,
        final String policyId,
        final String createdBy,
        final String updatedBy,
        final CouponWalletStatus status
    ) {
        return mongoTemplate.find(
            CouponWalletDocument.query(userId, orderId, policyId, createdBy, updatedBy, status),
            CouponWalletDocument.class
        );
    }
}

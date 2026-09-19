package im.dangmoo.benefit.infrastructure.data.coupon.wallet;

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

@Repository
public class CouponWalletMongoRepository {

    private final MongoTemplate mongoTemplate;

    public CouponWalletMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public CouponWallet save(final CouponWallet wallet) {
        return mongoTemplate.save(wallet);
    }

    public void insert(final CouponWallet wallet) {
        try {
            mongoTemplate.insert(wallet);
        } catch (final DuplicateKeyException ignored) {
        }
    }

    public Optional<CouponWallet> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, CouponWallet.class));
    }

    public Optional<CouponWallet> findByIdempotencyKey(final String idempotencyKey) {
        return Optional.ofNullable(
            mongoTemplate.findOne(CouponWallet.queryByIdempotencyKey(idempotencyKey), CouponWallet.class)
        );
    }

    public Set<String> findExistingIdempotencyKeys(final Collection<String> idempotencyKeys) {
        if (idempotencyKeys == null || idempotencyKeys.isEmpty()) {
            return Set.of();
        }
        final Query query = Query.query(Criteria.where(CouponWallet.IDEMPOTENCY_KEY).in(idempotencyKeys));
        query.fields().include(CouponWallet.IDEMPOTENCY_KEY);
        return mongoTemplate.find(query, CouponWallet.class).stream()
            .map(CouponWallet::getIdempotencyKey)
            .collect(Collectors.toCollection(HashSet::new));
    }

    public List<CouponWallet> findByUserId(final String userId) {
        return mongoTemplate.find(CouponWallet.queryByUserId(userId), CouponWallet.class);
    }

    public List<CouponWallet> findByUserIdAndStatus(final String userId, final CouponWalletStatus status) {
        return mongoTemplate.find(CouponWallet.queryByUserIdAndStatus(userId, status), CouponWallet.class);
    }

    public List<CouponWallet> search(
        final String userId,
        final String orderId,
        final String policyId,
        final String createdBy,
        final String updatedBy,
        final CouponWalletStatus status
    ) {
        return mongoTemplate.find(
            CouponWallet.query(userId, orderId, policyId, createdBy, updatedBy, status),
            CouponWallet.class
        );
    }
}

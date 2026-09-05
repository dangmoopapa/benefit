package im.dangmoo.benefit.domain.data.coupon.wallet;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class CouponWalletRepository {

    private static final String ID = "_id";
    private static final String STATUS = "status";
    private static final String ORDER_ID = "orderId";
    private static final String USED_AMOUNT = "usedAmount";
    private static final String USED_AT = "usedAt";
    private static final String RECOVERED_AT = "recoveredAt";
    private static final String UPDATED_BY = "updatedBy";
    private static final String UPDATED_AT = "updatedAt";

    private final MongoTemplate mongoTemplate;

    public CouponWalletRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CouponWallet> findAllByPolicyId(final String policyId) {
        return mongoTemplate.find(CouponWallet.queryByPolicyId(policyId), CouponWallet.class);
    }

    public List<CouponWallet> findAllByUserId(final String userId) {
        return mongoTemplate.find(CouponWallet.queryByUserId(userId), CouponWallet.class);
    }

    public Optional<CouponWallet> findById(final String walletId) {
        return Optional.ofNullable(mongoTemplate.findById(walletId, CouponWallet.class));
    }

    public CouponWallet save(final CouponWallet wallet) {
        return mongoTemplate.save(wallet);
    }

    /**
     * AVAILABLE → USED 조건부 갱신. 동시 사용 시 empty.
     */
    public Optional<CouponWallet> markUsed(
        final String walletId,
        final String orderId,
        final BigDecimal usedAmount,
        final String updatedBy
    ) {
        final Instant now = Instant.now();
        final Query query = Query.query(
            Criteria.where(ID).is(walletId).and(STATUS).is(CouponWalletStatus.AVAILABLE)
        );
        final Update update = new Update()
            .set(STATUS, CouponWalletStatus.USED)
            .set(ORDER_ID, orderId)
            .set(USED_AMOUNT, usedAmount)
            .set(USED_AT, now)
            .set(UPDATED_BY, updatedBy)
            .set(UPDATED_AT, now);
        return Optional.ofNullable(
            mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                CouponWallet.class
            )
        );
    }

    /**
     * USED → RECOVERED 조건부 갱신.
     */
    public Optional<CouponWallet> markRecovered(final String walletId, final String updatedBy) {
        final Instant now = Instant.now();
        final Query query = Query.query(
            Criteria.where(ID).is(walletId).and(STATUS).is(CouponWalletStatus.USED)
        );
        final Update update = new Update()
            .set(STATUS, CouponWalletStatus.RECOVERED)
            .set(RECOVERED_AT, now)
            .set(UPDATED_BY, updatedBy)
            .set(UPDATED_AT, now);
        return Optional.ofNullable(
            mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                CouponWallet.class
            )
        );
    }
}

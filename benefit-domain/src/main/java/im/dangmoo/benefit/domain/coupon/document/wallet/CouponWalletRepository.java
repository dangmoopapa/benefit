package im.dangmoo.benefit.domain.coupon.document.wallet;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CouponWalletRepository {

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

    public long countByPolicyId(final String policyId) {
        return mongoTemplate.count(CouponWallet.queryByPolicyId(policyId), CouponWallet.class);
    }

    public boolean existsByUserIdAndPolicyId(final String userId, final String policyId) {
        return mongoTemplate.exists(CouponWallet.queryByUserIdAndPolicyId(userId, policyId), CouponWallet.class);
    }

    public CouponWallet save(final CouponWallet wallet) {
        return mongoTemplate.save(wallet);
    }
}

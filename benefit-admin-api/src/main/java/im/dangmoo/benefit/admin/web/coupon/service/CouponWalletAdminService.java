package im.dangmoo.benefit.admin.web.coupon.service;

import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.domain.coupon.wallet.CouponWallet;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponWalletAdminService {

    private final MongoTemplate mongoTemplate;

    public CouponWalletAdminService(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CouponWalletResponse> listByPolicyId(final String policyId) {
        final Query query = CouponWallet.queryByPolicyId(policyId);
        return mongoTemplate.find(query, CouponWallet.class).stream()
            .map(CouponWalletResponse::of)
            .toList();
    }

    public List<CouponWalletResponse> listByUserId(final String userId) {
        final Query query = CouponWallet.queryByUserId(userId);
        return mongoTemplate.find(query, CouponWallet.class).stream()
            .map(CouponWalletResponse::of)
            .toList();
    }
}

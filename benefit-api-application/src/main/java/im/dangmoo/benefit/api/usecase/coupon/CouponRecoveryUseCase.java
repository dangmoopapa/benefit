package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.model.coupon.CouponRecoveryRequest;
import im.dangmoo.benefit.api.model.coupon.CouponRecoveryResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCacheRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponUsageStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletStatus;
import org.springframework.stereotype.Service;

@Service
public class CouponRecoveryUseCase {

    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponPolicyCacheRepository couponPolicyCacheRepository;
    private final CouponUsageStockRedisRepository couponUsageStockRedisRepository;

    public CouponRecoveryUseCase(
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponPolicyCacheRepository couponPolicyCacheRepository,
        final CouponUsageStockRedisRepository couponUsageStockRedisRepository
    ) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
        this.couponUsageStockRedisRepository = couponUsageStockRedisRepository;
    }

    public CouponRecoveryResponse recover(final String userId, final CouponRecoveryRequest request) {
        final CouponWalletDocument wallet = couponWalletMongoRepository.findById(request.walletId())
            .orElseThrow(ApiException::notFound);
        if (!userId.equals(wallet.getUserId())) {
            throw ApiException.notFound();
        }

        final CouponWalletStatus status = wallet.getStatus();
        if (status != CouponWalletStatus.USED) {
            throw ApiException.invalidStatus();
        }

        final CouponPolicyCache policy = couponPolicyCacheRepository.findByKey(wallet.getPolicyKey());
        if (policy == null) {
            throw ApiException.notFound();
        }

        if (!CouponIssueDomain.of(policy).isRecoverableAfterUse()) {
            throw ApiException.conditionNotSatisfied();
        }

        final CouponWalletDocument saved = couponWalletMongoRepository.save(wallet.recover(userId));
        couponUsageStockRedisRepository.decrement(wallet.getPolicyId());
        return CouponRecoveryResponse.of(saved);
    }
}

package im.dangmoo.benefit.admin.usecase.coupon;

import im.dangmoo.benefit.admin.model.coupon.wallet.CouponWalletRecoveryResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponRecoveryDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponUsageStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletStatus;
import org.springframework.stereotype.Service;

@Service
public class CouponWalletRecoveryUseCase {

    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponPolicyMongoRepository couponPolicyMongoRepository;
    private final CouponUsageStockRedisRepository couponUsageStockRedisRepository;

    public CouponWalletRecoveryUseCase(
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponPolicyMongoRepository couponPolicyMongoRepository,
        final CouponUsageStockRedisRepository couponUsageStockRedisRepository
    ) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
        this.couponUsageStockRedisRepository = couponUsageStockRedisRepository;
    }

    public CouponWalletRecoveryResponse execute(final String adminId, final String walletId) {
        final CouponWallet wallet = couponWalletMongoRepository.findById(walletId)
            .orElseThrow(ApiException::notFound);

        final CouponWalletStatus status = wallet.getStatus();
        if (status != CouponWalletStatus.USED) {
            throw ApiException.invalidStatus();
        }

        final CouponPolicy policy = couponPolicyMongoRepository.findByKey(wallet.getPolicyKey())
            .orElseThrow(ApiException::notFound);

        final boolean recoverable = CouponRecoveryDomain.of(policy.getLifecycleCondition()).isRecoverable();
        if (!recoverable) {
            throw ApiException.conditionNotSatisfied();
        }

        final CouponWallet saved = couponWalletMongoRepository.save(wallet.recover(adminId));
        couponUsageStockRedisRepository.decrement(wallet.getPolicyId());
        return CouponWalletRecoveryResponse.of(saved);
    }
}

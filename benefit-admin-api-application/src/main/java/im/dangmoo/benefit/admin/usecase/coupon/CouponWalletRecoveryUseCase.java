package im.dangmoo.benefit.admin.usecase.coupon;

import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletRecoveryResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyMongoRepository;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponUsageStockRedisRepository;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponWalletMongoRepository;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletStatus;
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

    public CouponWalletRecoveryResponse recover(final String adminId, final String walletId) {
        final CouponWalletDocument wallet = couponWalletMongoRepository.findById(walletId)
            .orElseThrow(ApiException::notFound);

        final CouponWalletStatus status = wallet.getStatus();
        if (status != CouponWalletStatus.USED) {
            throw ApiException.invalidStatus();
        }

        final CouponPolicyDocument policy = couponPolicyMongoRepository.findByKey(wallet.getPolicyKey())
            .orElseThrow(ApiException::notFound);

        if (!CouponIssueDomain.of(policy).isRecoverableAfterUse()) {
            throw ApiException.conditionNotSatisfied();
        }

        final CouponWalletDocument saved = couponWalletMongoRepository.save(wallet.recover(adminId));
        couponUsageStockRedisRepository.decrement(wallet.getPolicyId());
        return CouponWalletRecoveryResponse.of(saved);
    }
}

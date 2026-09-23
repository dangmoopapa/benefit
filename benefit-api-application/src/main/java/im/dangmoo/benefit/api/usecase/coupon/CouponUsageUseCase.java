package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.dto.coupon.CouponUsageRequest;
import im.dangmoo.benefit.api.dto.coupon.CouponUsageResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponApplyDomain;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponUsageDomain;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyCacheRepository;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponUsageStockRedisRepository;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponWalletMongoRepository;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CouponUsageUseCase {

    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponPolicyCacheRepository couponPolicyCacheRepository;
    private final CouponUsageStockRedisRepository couponUsageStockRedisRepository;

    public CouponUsageUseCase(
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponPolicyCacheRepository couponPolicyCacheRepository,
        final CouponUsageStockRedisRepository couponUsageStockRedisRepository
    ) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
        this.couponUsageStockRedisRepository = couponUsageStockRedisRepository;
    }

    public CouponUsageResponse use(final String userId, final CouponUsageRequest request) {
        final CouponWalletDocument wallet = couponWalletMongoRepository.findById(request.walletId())
            .orElseThrow(ApiException::notFound);
        if (!userId.equals(wallet.getUserId())) {
            throw ApiException.notFound();
        }

        final CouponWalletStatus status = wallet.getStatus();
        if (status != CouponWalletStatus.AVAILABLE) {
            throw ApiException.invalidStatus();
        }

        final CouponPolicyCache policy = couponPolicyCacheRepository.findByKey(wallet.getPolicyKey());
        if (policy == null) {
            throw ApiException.notFound();
        }

        final Instant now = Instant.now();
        if (!CouponIssueDomain.of(policy).isOpenAt(now)) {
            throw ApiException.conditionNotSatisfied();
        }

        final long usedCount = couponUsageStockRedisRepository.get(wallet.getPolicyId());
        final boolean usable = CouponUsageDomain.of(policy).isUsableAt(
            now,
            wallet.getIssuedAt(),
            wallet.getExpiresAt(),
            usedCount,
            request.paymentAmount()
        );
        final boolean applicable = CouponApplyDomain.of(policy).isApplicableTo(
            request.productId(),
            request.categoryId(),
            request.brandId(),
            request.segmentId()
        );
        if (!usable || !applicable) {
            throw ApiException.conditionNotSatisfied();
        }

        final CouponWalletDocument saved = couponWalletMongoRepository.save(
            wallet.use(request.orderId(), request.usedAmount(), userId)
        );
        couponUsageStockRedisRepository.increment(wallet.getPolicyId());
        return CouponUsageResponse.of(saved);
    }
}

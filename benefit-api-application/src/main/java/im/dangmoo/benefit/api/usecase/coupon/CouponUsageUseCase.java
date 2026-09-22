package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.model.coupon.CouponUsageRequest;
import im.dangmoo.benefit.api.model.coupon.CouponUsageResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponUsageDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CachedCouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCacheRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponUsageStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletStatus;
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
        final CouponWallet wallet = couponWalletMongoRepository.findById(request.walletId())
            .orElseThrow(ApiException::notFound);
        if (!userId.equals(wallet.getUserId())) {
            throw ApiException.notFound();
        }

        final CouponWalletStatus status = wallet.getStatus();
        if (status != CouponWalletStatus.AVAILABLE) {
            throw ApiException.invalidStatus();
        }

        final CachedCouponPolicy policy = couponPolicyCacheRepository.findByKey(wallet.getPolicyKey());
        if (policy == null) {
            throw ApiException.notFound();
        }

        final Instant now = Instant.now();
        final boolean issueSatisfied = CouponIssueDomain.of(policy.issueCondition()).isSatisfiedAt(now);
        if (!issueSatisfied) {
            throw ApiException.conditionNotSatisfied();
        }

        final long usedCount = couponUsageStockRedisRepository.get(wallet.getPolicyId());
        final boolean usageSatisfied = CouponUsageDomain.of(
            policy.usageCondition(),
            policy.applyCondition()
        ).isSatisfied(
            wallet.getIssuedAt(),
            wallet.getExpiresAt(),
            now,
            usedCount,
            request.paymentAmount(),
            request.productId(),
            request.categoryId(),
            request.brandId(),
            request.segmentId()
        );
        if (!usageSatisfied) {
            throw ApiException.conditionNotSatisfied();
        }

        final CouponWallet saved = couponWalletMongoRepository.save(
            wallet.use(request.orderId(), request.usedAmount(), userId)
        );
        couponUsageStockRedisRepository.increment(wallet.getPolicyId());
        return CouponUsageResponse.of(saved);
    }
}

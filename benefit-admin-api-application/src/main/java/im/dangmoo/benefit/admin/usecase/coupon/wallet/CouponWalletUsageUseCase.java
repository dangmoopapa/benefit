package im.dangmoo.benefit.admin.usecase.coupon.wallet;

import im.dangmoo.benefit.admin.model.coupon.wallet.CouponWalletUsageRequest;
import im.dangmoo.benefit.admin.model.coupon.wallet.CouponWalletUsageResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponUsageDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponUsageStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CouponWalletUsageUseCase {

    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponPolicyMongoRepository couponPolicyMongoRepository;
    private final CouponUsageStockRedisRepository couponUsageStockRedisRepository;

    public CouponWalletUsageUseCase(
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponPolicyMongoRepository couponPolicyMongoRepository,
        final CouponUsageStockRedisRepository couponUsageStockRedisRepository
    ) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
        this.couponUsageStockRedisRepository = couponUsageStockRedisRepository;
    }

    public CouponWalletUsageResponse execute(
        final String adminId,
        final String walletId,
        final CouponWalletUsageRequest request
    ) {
        final CouponWallet wallet = couponWalletMongoRepository.findById(walletId)
            .orElseThrow(ApiException::notFound);

        final CouponWalletStatus status = wallet.getStatus();
        if (status != CouponWalletStatus.AVAILABLE) {
            throw ApiException.invalidStatus();
        }

        final CouponPolicy policy = couponPolicyMongoRepository.findByKey(wallet.getPolicyKey())
            .orElseThrow(ApiException::notFound);

        final Instant now = Instant.now();
        final boolean issueSatisfied = CouponIssueDomain.of(policy.getIssueCondition()).isSatisfiedAt(now);
        if (!issueSatisfied) {
            throw ApiException.conditionNotSatisfied();
        }

        final long usedCount = couponUsageStockRedisRepository.get(wallet.getPolicyId());
        final boolean usageSatisfied = CouponUsageDomain.of(
            policy.getUsageCondition(),
            policy.getApplyCondition()
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
            wallet.use(request.orderId(), request.usedAmount(), adminId)
        );
        couponUsageStockRedisRepository.increment(wallet.getPolicyId());
        return CouponWalletUsageResponse.of(saved);
    }
}

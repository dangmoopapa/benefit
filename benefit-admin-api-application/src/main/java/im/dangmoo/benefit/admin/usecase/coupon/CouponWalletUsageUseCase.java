package im.dangmoo.benefit.admin.usecase.coupon;

import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletUsageRequest;
import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletUsageResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponApplyDomain;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponUsageDomain;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyMongoRepository;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponUsageStockRedisRepository;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponWalletMongoRepository;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletStatus;
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

    public CouponWalletUsageResponse use(
        final String adminId,
        final String walletId,
        final CouponWalletUsageRequest request
    ) {
        final CouponWalletDocument wallet = couponWalletMongoRepository.findById(walletId)
            .orElseThrow(ApiException::notFound);

        final CouponWalletStatus status = wallet.getStatus();
        if (status != CouponWalletStatus.AVAILABLE) {
            throw ApiException.invalidStatus();
        }

        final CouponPolicyDocument policy = couponPolicyMongoRepository.findByKey(wallet.getPolicyKey())
            .orElseThrow(ApiException::notFound);

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
            wallet.use(request.orderId(), request.usedAmount(), adminId)
        );
        couponUsageStockRedisRepository.increment(wallet.getPolicyId());
        return CouponWalletUsageResponse.of(saved);
    }
}

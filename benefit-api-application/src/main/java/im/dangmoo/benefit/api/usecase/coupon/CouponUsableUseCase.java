package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.model.coupon.CouponUsableRequest;
import im.dangmoo.benefit.api.model.coupon.CouponUsableResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CouponUsableUseCase {

    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponPolicyCacheRepository couponPolicyCacheRepository;
    private final CouponUsageStockRedisRepository couponUsageStockRedisRepository;

    public CouponUsableUseCase(
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponPolicyCacheRepository couponPolicyCacheRepository,
        final CouponUsageStockRedisRepository couponUsageStockRedisRepository
    ) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
        this.couponUsageStockRedisRepository = couponUsageStockRedisRepository;
    }

    public CouponUsableResponse execute(final String userId, final CouponUsableRequest request) {
        final List<CouponWallet> wallets = couponWalletMongoRepository.findByUserIdAndStatus(
            userId,
            CouponWalletStatus.AVAILABLE
        );
        if (wallets.isEmpty()) {
            return CouponUsableResponse.of(List.of());
        }

        final List<String> policyIds = wallets.stream()
            .map(CouponWallet::getPolicyId)
            .distinct()
            .toList();
        final Map<String, Long> usedCounts = couponUsageStockRedisRepository.get(policyIds);

        final Instant now = Instant.now();
        final List<CouponUsableResponse.Item> items = new ArrayList<>();

        for (final CouponWallet wallet : wallets) {
            final CachedCouponPolicy policy = couponPolicyCacheRepository.findByKey(wallet.getPolicyKey());
            if (policy == null) {
                continue;
            }
            final boolean issueSatisfied = CouponIssueDomain.of(policy.issueCondition()).isSatisfiedAt(now);
            if (!issueSatisfied) {
                continue;
            }

            final long usedCount = usedCounts.getOrDefault(wallet.getPolicyId(), 0L);
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
                continue;
            }
            items.add(new CouponUsableResponse.Item(
                wallet.getId(),
                wallet.getPolicyId(),
                wallet.getPolicyKey(),
                policy.name(),
                wallet.getExpiresAt()
            ));
        }
        return CouponUsableResponse.of(items);
    }
}

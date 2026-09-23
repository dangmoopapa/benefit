package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.dto.coupon.CouponUsableRequest;
import im.dangmoo.benefit.api.dto.coupon.CouponUsableResponse;
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

    public CouponUsableResponse usable(final String userId, final CouponUsableRequest request) {
        final List<CouponWalletDocument> wallets = couponWalletMongoRepository.findByUserIdAndStatus(
            userId,
            CouponWalletStatus.AVAILABLE
        );
        if (wallets.isEmpty()) {
            return CouponUsableResponse.of(List.of());
        }

        final List<String> policyIds = wallets.stream()
            .map(CouponWalletDocument::getPolicyId)
            .distinct()
            .toList();
        final Map<String, Long> usedCounts = couponUsageStockRedisRepository.get(policyIds);

        final Instant now = Instant.now();
        final List<CouponUsableResponse.Item> items = new ArrayList<>();

        for (final CouponWalletDocument wallet : wallets) {
            final CouponPolicyCache policy = couponPolicyCacheRepository.findByKey(wallet.getPolicyKey());
            if (policy == null) {
                continue;
            }
            if (!CouponIssueDomain.of(policy).isOpenAt(now)) {
                continue;
            }

            final long usedCount = usedCounts.getOrDefault(wallet.getPolicyId(), 0L);
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

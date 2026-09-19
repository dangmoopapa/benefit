package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.model.coupon.VoucherCouponBoxRequest;
import im.dangmoo.benefit.api.model.coupon.VoucherCouponBoxResponse;
import im.dangmoo.benefit.domain.coupon.CouponApplyDomain;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponWalletDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class VoucherCouponBoxUseCase {

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;
    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponIssueStockRedisRepository couponIssueStockRedisRepository;

    public VoucherCouponBoxUseCase(
        final CouponPolicyMongoRepository couponPolicyMongoRepository,
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponIssueStockRedisRepository couponIssueStockRedisRepository
    ) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponIssueStockRedisRepository = couponIssueStockRedisRepository;
    }

    public VoucherCouponBoxResponse execute(final String userId, final VoucherCouponBoxRequest request) {
        final List<CouponPolicy> policies = couponPolicyMongoRepository.findActiveVouchers(
            request.productId(),
            request.brandId()
        );
        if (policies.isEmpty()) {
            return VoucherCouponBoxResponse.of(List.of());
        }

        final List<String> idempotencyKeys = policies.stream()
            .map(policy -> CouponWalletDomain.idempotencyKey(policy.getId(), userId))
            .toList();
        final Set<String> issuedKeys = couponWalletMongoRepository.findExistingIdempotencyKeys(idempotencyKeys);

        final List<String> policyIds = policies.stream()
            .map(CouponPolicy::getId)
            .toList();
        final Map<String, Long> issuedCounts = couponIssueStockRedisRepository.get(policyIds);

        final Instant now = Instant.now();
        final List<VoucherCouponBoxResponse.Item> items = new ArrayList<>();

        for (final CouponPolicy policy : policies) {
            if (!CouponApplyDomain.of(policy.getApplyCondition()).belongsTo(request.productId(), request.brandId())) {
                continue;
            }
            if (issuedKeys.contains(CouponWalletDomain.idempotencyKey(policy.getId(), userId))) {
                continue;
            }

            final long issuedCount = issuedCounts.getOrDefault(policy.getId(), 0L);
            final boolean issueSatisfied = CouponIssueDomain.of(policy.getIssueCondition())
                .isSatisfied(now, issuedCount);
            if (!issueSatisfied) {
                continue;
            }

            items.add(VoucherCouponBoxResponse.Item.of(policy));
        }

        return VoucherCouponBoxResponse.of(items);
    }
}

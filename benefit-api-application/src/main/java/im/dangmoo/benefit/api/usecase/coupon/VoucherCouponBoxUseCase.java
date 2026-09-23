package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.dto.coupon.VoucherCouponBoxRequest;
import im.dangmoo.benefit.api.dto.coupon.VoucherCouponBoxResponse;
import im.dangmoo.benefit.domain.coupon.CouponApplyDomain;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyMongoRepository;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponWalletMongoRepository;
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

    public VoucherCouponBoxResponse box(final String userId, final VoucherCouponBoxRequest request) {
        final List<CouponPolicyDocument> policies = couponPolicyMongoRepository.findActiveVouchers(
            request.productId(),
            request.brandId()
        );
        if (policies.isEmpty()) {
            return VoucherCouponBoxResponse.of(List.of());
        }

        final Instant now = Instant.now();
        final List<String> issueKeys = policies.stream()
            .map(policy -> CouponIssueDomain.of(policy).issueKeyFor(policy.getId(), userId, now))
            .toList();
        final Set<String> issuedKeys = couponWalletMongoRepository.findExistingIdempotencyKeys(issueKeys);

        final List<String> policyIds = policies.stream()
            .map(CouponPolicyDocument::getId)
            .toList();
        final Map<String, Long> issuedCounts = couponIssueStockRedisRepository.get(policyIds);

        final List<VoucherCouponBoxResponse.Item> items = new ArrayList<>();

        for (final CouponPolicyDocument policy : policies) {
            if (!CouponApplyDomain.of(policy).covers(request.productId(), request.brandId())) {
                continue;
            }

            final CouponIssueDomain couponIssue = CouponIssueDomain.of(policy);
            final String issueKey = couponIssue.issueKeyFor(policy.getId(), userId, now);
            if (issuedKeys.contains(issueKey)) {
                continue;
            }

            final long issuedCount = issuedCounts.getOrDefault(policy.getId(), 0L);
            final var issuability = couponIssue.issuabilityAt(now, false, issuedCount);
            if (issuability != CouponIssueDomain.Issuability.ISSUABLE) {
                continue;
            }

            items.add(VoucherCouponBoxResponse.Item.of(policy));
        }

        return VoucherCouponBoxResponse.of(items);
    }
}

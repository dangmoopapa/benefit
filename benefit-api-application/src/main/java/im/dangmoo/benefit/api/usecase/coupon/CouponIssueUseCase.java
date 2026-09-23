package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.model.coupon.CouponIssueRequest;
import im.dangmoo.benefit.api.model.coupon.CouponIssueResponse;
import im.dangmoo.benefit.api.model.coupon.MarketingCouponIssueRequest;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeType;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCacheRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.changed.CouponPolicyChangedPublication;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.changed.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CouponIssueUseCase {

    private final CouponPolicyCacheRepository couponPolicyCacheRepository;
    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponIssueStockRedisRepository couponIssueStockRedisRepository;
    private final CouponPolicyChangedPublisher couponPolicyChangedPublisher;
    private final CouponCodeMongoRepository couponCodeMongoRepository;

    public CouponIssueUseCase(
        final CouponPolicyCacheRepository couponPolicyCacheRepository,
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponIssueStockRedisRepository couponIssueStockRedisRepository,
        final CouponPolicyChangedPublisher couponPolicyChangedPublisher,
        final CouponCodeMongoRepository couponCodeMongoRepository
    ) {
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponIssueStockRedisRepository = couponIssueStockRedisRepository;
        this.couponPolicyChangedPublisher = couponPolicyChangedPublisher;
        this.couponCodeMongoRepository = couponCodeMongoRepository;
    }

    public CouponIssueResponse issue(final String userId, final CouponIssueRequest request) {
        final CouponPolicyCache policy = couponPolicyCacheRepository.findByKey(request.policyKey());
        if (policy == null) {
            throw ApiException.notFound();
        }
        return issue(userId, policy);
    }

    public CouponIssueResponse issueMarketing(final String userId, final MarketingCouponIssueRequest request) {
        final CouponCodeDocument code = couponCodeMongoRepository.findByCode(request.code())
            .orElseThrow(ApiException::notFound);
        final CouponPolicyCache policy = couponPolicyCacheRepository.findByKey(code.getPolicyKey());
        if (policy == null) {
            throw ApiException.notFound();
        }
        if (code.getType() == CouponCodeType.RANDOM) {
            couponCodeMongoRepository.redeem(code.getId(), userId)
                .orElseThrow(ApiException::stockExhausted);
        } else if (!code.isAvailable()) {
            throw ApiException.stockExhaustedCoupon();
        }
        return issue(userId, policy);
    }

    private CouponIssueResponse issue(final String userId, final CouponPolicyCache policy) {
        final Instant now = Instant.now();
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(policy);
        final String issueKey = couponIssue.issueKeyFor(policy.id(), userId, now);
        final boolean alreadyIssued = couponWalletMongoRepository.findByIdempotencyKey(issueKey).isPresent();
        final long issuedCount = couponIssueStockRedisRepository.get(policy.id());

        switch (couponIssue.issuabilityAt(now, alreadyIssued, issuedCount)) {
            case ALREADY_ISSUED -> throw ApiException.alreadyIssuedCoupon();
            case STOCK_EXHAUSTED -> throw ApiException.stockExhaustedCoupon();
            case POLICY_INACTIVE, OUT_OF_PERIOD -> throw ApiException.policyIssueCoupon();
            case ISSUABLE -> {
            }
        }

        final CouponWalletDocument saved = couponWalletMongoRepository.save(
            CouponWalletDocument.create(
                userId,
                policy.id(),
                policy.key(),
                issueKey,
                couponIssue.expiresAtFrom(now),
                userId
            )
        );
        final long issuedCountAfter = couponIssueStockRedisRepository.increment(policy.id());
        if (couponIssue.isLastIssue(issuedCountAfter)) {
            couponPolicyChangedPublisher.publish(CouponPolicyChangedPublication.ofExhausted(policy));
        }
        return CouponIssueResponse.of(saved);
    }
}

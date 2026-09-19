package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.model.coupon.CouponIssueRequest;
import im.dangmoo.benefit.api.model.coupon.CouponIssueResponse;
import im.dangmoo.benefit.api.model.coupon.MarketingCouponIssueRequest;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponExhaustionDomain;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponUsageDomain;
import im.dangmoo.benefit.domain.coupon.CouponWalletDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCode;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeType;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CachedCouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCacheRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedEvent;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;
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

    public CouponIssueResponse execute(final String userId, final CouponIssueRequest request) {
        final CachedCouponPolicy policy = couponPolicyCacheRepository.findByKey(request.policyKey());
        if (policy == null) {
            throw ApiException.notFound();
        }
        return execute(userId, policy);
    }

    public CouponIssueResponse executeMarketing(final String userId, final MarketingCouponIssueRequest request) {
        final CouponCode code = couponCodeMongoRepository.findByCode(request.code())
            .orElseThrow(ApiException::notFound);
        final CachedCouponPolicy policy = couponPolicyCacheRepository.findByKey(code.getPolicyKey());
        if (policy == null) {
            throw ApiException.notFound();
        }
        if (code.getType() == CouponCodeType.RANDOM) {
            couponCodeMongoRepository.redeem(code.getId(), userId)
                .orElseThrow(ApiException::stockExhausted);
        } else if (!code.isAvailable()) {
            throw ApiException.stockExhausted();
        }
        return execute(userId, policy);
    }

    private CouponIssueResponse execute(final String userId, final CachedCouponPolicy policy) {
        if (policy.status().isNotActive()) {
            throw ApiException.policyIssue();
        }

        final CouponIssueDomain issueDomain = CouponIssueDomain.of(policy.issueCondition());
        final CouponExhaustionDomain exhaustionDomain = CouponExhaustionDomain.of(policy.issueCondition());
        final String idempotencyKey = CouponWalletDomain.idempotencyKey(policy.id(), userId);
        final boolean alreadyIssued = couponWalletMongoRepository.findByIdempotencyKey(idempotencyKey).isPresent();
        if (alreadyIssued) {
            throw ApiException.alreadyIssued();
        }

        final Instant now = Instant.now();
        final long issuedCount = couponIssueStockRedisRepository.get(policy.id());
        if (exhaustionDomain.isExhausted(issuedCount)) {
            throw ApiException.stockExhausted();
        }
        if (!issueDomain.isSatisfiedAt(now)) {
            throw ApiException.policyIssue();
        }

        final Instant expiresAt = CouponUsageDomain.of(
            policy.usageCondition(),
            policy.applyCondition()
        ).resolveExpiresAt(now);

        final CouponWallet wallet = CouponWallet.create(
            userId,
            policy.id(),
            policy.key(),
            idempotencyKey,
            expiresAt,
            userId
        );
        final CouponWallet saved = couponWalletMongoRepository.save(wallet);
        final long issuedCountAfter = couponIssueStockRedisRepository.increment(policy.id());
        if (exhaustionDomain.isJustExhausted(issuedCountAfter)) {
            couponPolicyChangedPublisher.publish(CouponPolicyChangedEvent.ofExhausted(policy));
        }
        return CouponIssueResponse.of(saved);
    }
}

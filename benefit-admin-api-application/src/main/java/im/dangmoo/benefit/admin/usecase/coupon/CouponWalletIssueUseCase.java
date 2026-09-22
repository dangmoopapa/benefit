package im.dangmoo.benefit.admin.usecase.coupon;

import im.dangmoo.benefit.admin.model.coupon.wallet.CouponWalletIssueRequest;
import im.dangmoo.benefit.admin.model.coupon.wallet.CouponWalletIssueResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponExhaustionDomain;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponUsageDomain;
import im.dangmoo.benefit.domain.coupon.CouponWalletDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedEvent;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CouponWalletIssueUseCase {

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;
    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponIssueStockRedisRepository couponIssueStockRedisRepository;
    private final CouponPolicyChangedPublisher couponPolicyChangedPublisher;

    public CouponWalletIssueUseCase(
        final CouponPolicyMongoRepository couponPolicyMongoRepository,
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponIssueStockRedisRepository couponIssueStockRedisRepository,
        final CouponPolicyChangedPublisher couponPolicyChangedPublisher
    ) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponIssueStockRedisRepository = couponIssueStockRedisRepository;
        this.couponPolicyChangedPublisher = couponPolicyChangedPublisher;
    }

    public CouponWalletIssueResponse issue(final String adminId, final CouponWalletIssueRequest request) {
        final String policyKey = request.policyKey();
        final String userId = request.userId();
        final CouponPolicy policy = couponPolicyMongoRepository.findByKey(policyKey)
            .orElseThrow(ApiException::notFound);

        final CouponPolicyStatus status = policy.getStatus();
        if (status.isNotActive()) {
            throw ApiException.invalidStatus();
        }

        final Instant now = Instant.now();
        final CouponIssueDomain issueDomain = CouponIssueDomain.of(policy.getIssueCondition());
        final CouponExhaustionDomain exhaustionDomain = CouponExhaustionDomain.of(policy.getIssueCondition());
        final long issuedCount = couponIssueStockRedisRepository.get(policy.getId());
        if (!issueDomain.isSatisfied(now, issuedCount)) {
            throw ApiException.conditionNotSatisfied();
        }

        final String idempotencyKey = CouponWalletDomain.idempotencyKey(policy.getId(), userId);
        final boolean exists = couponWalletMongoRepository.findByIdempotencyKey(idempotencyKey).isPresent();
        if (exists) {
            throw ApiException.duplicateKey();
        }

        final Instant expiresAt = CouponUsageDomain.of(
            policy.getUsageCondition(),
            policy.getApplyCondition()
        ).resolveExpiresAt(now);

        final CouponWallet wallet = CouponWallet.create(
            userId,
            policy.getId(),
            policy.getKey(),
            idempotencyKey,
            expiresAt,
            adminId
        );
        final CouponWallet saved = couponWalletMongoRepository.save(wallet);
        final long issuedCountAfter = couponIssueStockRedisRepository.increment(policy.getId());
        if (exhaustionDomain.isJustExhausted(issuedCountAfter)) {
            couponPolicyChangedPublisher.publish(CouponPolicyChangedEvent.ofExhausted(policy));
        }
        return CouponWalletIssueResponse.of(saved);
    }
}

package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.model.coupon.CouponIssueRequest;
import im.dangmoo.benefit.api.model.coupon.CouponIssueResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponExhaustionDomain;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponUsageDomain;
import im.dangmoo.benefit.domain.coupon.CouponWalletDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CachedCouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCacheRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedEvent;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponTimeAttackIssueResult;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponTimeAttackIssueScript;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletIssueEvent;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.TimeAttackCouponWalletIssuePublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CouponTimeAttackIssueUseCase {

    private final CouponPolicyCacheRepository couponPolicyCacheRepository;
    private final CouponTimeAttackIssueScript couponTimeAttackIssueScript;
    private final TimeAttackCouponWalletIssuePublisher timeAttackCouponWalletIssuePublisher;
    private final CouponPolicyChangedPublisher couponPolicyChangedPublisher;

    public CouponTimeAttackIssueUseCase(
        final CouponPolicyCacheRepository couponPolicyCacheRepository,
        final CouponTimeAttackIssueScript couponTimeAttackIssueScript,
        final TimeAttackCouponWalletIssuePublisher timeAttackCouponWalletIssuePublisher,
        final CouponPolicyChangedPublisher couponPolicyChangedPublisher
    ) {
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
        this.couponTimeAttackIssueScript = couponTimeAttackIssueScript;
        this.timeAttackCouponWalletIssuePublisher = timeAttackCouponWalletIssuePublisher;
        this.couponPolicyChangedPublisher = couponPolicyChangedPublisher;
    }

    public CouponIssueResponse execute(final String userId, final CouponIssueRequest request) {
        final CachedCouponPolicy policy = couponPolicyCacheRepository.findByKey(request.policyKey());
        if (policy == null) {
            throw ApiException.notFound();
        }
        if (policy.status().isNotActive()) {
            throw ApiException.policyIssue();
        }

        final CouponIssueDomain issueDomain = CouponIssueDomain.of(policy.issueCondition());
        final CouponExhaustionDomain exhaustionDomain = CouponExhaustionDomain.of(policy.issueCondition());
        final Instant now = Instant.now();
        if (!issueDomain.isSatisfiedAt(now)) {
            throw ApiException.policyIssue();
        }

        final String idempotencyKey = CouponWalletDomain.idempotencyKey(policy.id(), userId);
        final CouponTimeAttackIssueResult result = couponTimeAttackIssueScript.execute(
            policy.id(),
            idempotencyKey,
            issueDomain.getStockQuantity()
        );
        if (result.isAlreadyIssued()) {
            throw ApiException.alreadyIssued();
        }
        if (result.isSoldOut()) {
            throw ApiException.stockExhausted();
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
        timeAttackCouponWalletIssuePublisher.publish(CouponWalletIssueEvent.of(wallet));
        if (exhaustionDomain.isJustExhausted(result.issuedCount())) {
            couponPolicyChangedPublisher.publish(CouponPolicyChangedEvent.ofExhausted(policy));
        }
        return CouponIssueResponse.of(wallet);
    }
}

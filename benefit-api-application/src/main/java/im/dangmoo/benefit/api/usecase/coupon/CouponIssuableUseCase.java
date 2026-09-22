package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.model.coupon.CouponIssuableRequest;
import im.dangmoo.benefit.api.model.coupon.CouponIssuableResponse;
import im.dangmoo.benefit.api.usecase.ApiMessage;
import im.dangmoo.benefit.domain.coupon.CouponExhaustionDomain;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponWalletDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CachedCouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCacheRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponTimeAttackIssueScript;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CouponIssuableUseCase {

    private final CouponPolicyCacheRepository couponPolicyCacheRepository;
    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponIssueStockRedisRepository couponIssueStockRedisRepository;
    private final CouponTimeAttackIssueScript couponTimeAttackIssueScript;

    public CouponIssuableUseCase(
        final CouponPolicyCacheRepository couponPolicyCacheRepository,
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponIssueStockRedisRepository couponIssueStockRedisRepository,
        final CouponTimeAttackIssueScript couponTimeAttackIssueScript
    ) {
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponIssueStockRedisRepository = couponIssueStockRedisRepository;
        this.couponTimeAttackIssueScript = couponTimeAttackIssueScript;
    }

    public CouponIssuableResponse execute(
        final String userId,
        final CouponIssuableRequest request,
        final boolean isTimeAttack
    ) {
        final CachedCouponPolicy policy = couponPolicyCacheRepository.findByKey(request.policyKey());
        if (policy == null) {
            return CouponIssuableResponse.ofNotIssuable(ApiMessage.POLICY_ISSUE_COUPON, isTimeAttack);
        }
        if (policy.status().isNotActive()) {
            return CouponIssuableResponse.ofNotIssuable(ApiMessage.POLICY_ISSUE_COUPON, isTimeAttack);
        }

        final CouponIssueDomain issueDomain = CouponIssueDomain.of(policy.issueCondition());
        final CouponExhaustionDomain exhaustionDomain = CouponExhaustionDomain.of(policy.issueCondition());
        final String idempotencyKey = CouponWalletDomain.idempotencyKey(policy.id(), userId);
        final boolean alreadyIssued = isTimeAttack
            ? couponTimeAttackIssueScript.hasIssued(idempotencyKey)
            : couponWalletMongoRepository.findByIdempotencyKey(idempotencyKey).isPresent();
        if (alreadyIssued) {
            return CouponIssuableResponse.ofNotIssuable(ApiMessage.ALREADY_ISSUED_COUPON, isTimeAttack);
        }

        final Instant now = Instant.now();
        final long issuedCount = couponIssueStockRedisRepository.get(policy.id());
        if (exhaustionDomain.isExhausted(issuedCount)) {
            return CouponIssuableResponse.ofNotIssuable(ApiMessage.STOCK_EXHAUSTED_COUPON, isTimeAttack);
        }
        if (!issueDomain.isSatisfiedAt(now)) {
            return CouponIssuableResponse.ofNotIssuable(ApiMessage.POLICY_ISSUE_COUPON, isTimeAttack);
        }

        return CouponIssuableResponse.ofIssuable(isTimeAttack);
    }
}

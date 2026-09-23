package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.model.coupon.CouponIssuableRequest;
import im.dangmoo.benefit.api.model.coupon.CouponIssuableResponse;
import im.dangmoo.benefit.api.usecase.ApiMessage;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCache;
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

    public CouponIssuableResponse issuable(
        final String userId,
        final CouponIssuableRequest request,
        final boolean isTimeAttack
    ) {
        final CouponPolicyCache policy = couponPolicyCacheRepository.findByKey(request.policyKey());
        if (policy == null) {
            return CouponIssuableResponse.ofNotIssuable(ApiMessage.POLICY_ISSUE_COUPON, isTimeAttack);
        }

        final Instant now = Instant.now();
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(policy);
        final String issueKey = couponIssue.issueKeyFor(policy.id(), userId, now);
        final boolean alreadyIssued = isTimeAttack
            ? couponTimeAttackIssueScript.hasIssued(issueKey)
            : couponWalletMongoRepository.findByIdempotencyKey(issueKey).isPresent();
        final long issuedCount = couponIssueStockRedisRepository.get(policy.id());

        return switch (couponIssue.issuabilityAt(now, alreadyIssued, issuedCount)) {
            case ALREADY_ISSUED -> CouponIssuableResponse.ofNotIssuable(ApiMessage.ALREADY_ISSUED_COUPON, isTimeAttack);
            case STOCK_EXHAUSTED -> CouponIssuableResponse.ofNotIssuable(ApiMessage.STOCK_EXHAUSTED_COUPON, isTimeAttack);
            case POLICY_INACTIVE, OUT_OF_PERIOD ->
                CouponIssuableResponse.ofNotIssuable(ApiMessage.POLICY_ISSUE_COUPON, isTimeAttack);
            case ISSUABLE -> CouponIssuableResponse.ofIssuable(isTimeAttack);
        };
    }
}

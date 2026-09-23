package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.dto.coupon.CouponIssueRequest;
import im.dangmoo.benefit.api.dto.coupon.CouponIssueResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyCacheRepository;
import im.dangmoo.benefit.data.entity.coupon.policy.changed.CouponPolicyChangedPublication;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.data.entity.coupon.stock.CouponTimeAttackIssueResult;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponTimeAttackIssueScript;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.data.entity.coupon.wallet.issue.CouponTimaAttackIssuePublication;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponTimeAttackIssuePublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CouponTimeAttackIssueUseCase {

    private final CouponPolicyCacheRepository couponPolicyCacheRepository;
    private final CouponTimeAttackIssueScript couponTimeAttackIssueScript;
    private final CouponTimeAttackIssuePublisher couponTimeAttackIssuePublisher;
    private final CouponPolicyChangedPublisher couponPolicyChangedPublisher;

    public CouponTimeAttackIssueUseCase(
        final CouponPolicyCacheRepository couponPolicyCacheRepository,
        final CouponTimeAttackIssueScript couponTimeAttackIssueScript,
        final CouponTimeAttackIssuePublisher couponTimeAttackIssuePublisher,
        final CouponPolicyChangedPublisher couponPolicyChangedPublisher
    ) {
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
        this.couponTimeAttackIssueScript = couponTimeAttackIssueScript;
        this.couponTimeAttackIssuePublisher = couponTimeAttackIssuePublisher;
        this.couponPolicyChangedPublisher = couponPolicyChangedPublisher;
    }

    public CouponIssueResponse issue(final String userId, final CouponIssueRequest request) {
        final CouponPolicyCache policy = couponPolicyCacheRepository.findByKey(request.policyKey());
        if (policy == null) {
            throw ApiException.notFound();
        }

        final Instant now = Instant.now();
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(policy);
        switch (couponIssue.issuabilityAt(now, false, 0L)) {
            case POLICY_INACTIVE, OUT_OF_PERIOD -> throw ApiException.policyIssueCoupon();
            case ALREADY_ISSUED, STOCK_EXHAUSTED, ISSUABLE -> {
            }
        }

        final String issueKey = couponIssue.issueKeyFor(policy.id(), userId, now);
        final CouponTimeAttackIssueResult result = couponTimeAttackIssueScript.execute(
            policy.id(),
            issueKey,
            couponIssue.stockQuantity()
        );
        if (result.isAlreadyIssued()) {
            throw ApiException.alreadyIssuedCoupon();
        }
        if (result.isSoldOut()) {
            throw ApiException.stockExhaustedCoupon();
        }

        final CouponWalletDocument wallet = CouponWalletDocument.create(
            userId,
            policy.id(),
            policy.key(),
            issueKey,
            couponIssue.expiresAtFrom(now),
            userId
        );
        couponTimeAttackIssuePublisher.publish(CouponTimaAttackIssuePublication.of(wallet));
        if (couponIssue.isLastIssue(result.issuedCount())) {
            couponPolicyChangedPublisher.publish(CouponPolicyChangedPublication.ofExhausted(policy));
        }
        return CouponIssueResponse.of(wallet);
    }
}

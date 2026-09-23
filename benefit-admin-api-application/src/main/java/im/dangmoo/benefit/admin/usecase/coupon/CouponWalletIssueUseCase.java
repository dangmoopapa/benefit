package im.dangmoo.benefit.admin.usecase.coupon;

import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletIssueRequest;
import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletIssueResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.data.entity.coupon.policy.changed.CouponPolicyChangedPublication;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyMongoRepository;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponWalletMongoRepository;
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
        final CouponPolicyDocument policy = couponPolicyMongoRepository.findByKey(request.policyKey())
            .orElseThrow(ApiException::notFound);

        final Instant now = Instant.now();
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(policy);
        final String issueKey = couponIssue.issueKeyFor(policy.getId(), request.userId(), now);
        final boolean alreadyIssued = couponWalletMongoRepository.findByIdempotencyKey(issueKey).isPresent();
        final long issuedCount = couponIssueStockRedisRepository.get(policy.getId());

        switch (couponIssue.issuabilityAt(now, alreadyIssued, issuedCount)) {
            case ALREADY_ISSUED -> throw ApiException.duplicateKey();
            case STOCK_EXHAUSTED, OUT_OF_PERIOD -> throw ApiException.conditionNotSatisfied();
            case POLICY_INACTIVE -> throw ApiException.invalidStatus();
            case ISSUABLE -> {
            }
        }

        final CouponWalletDocument saved = couponWalletMongoRepository.save(
            CouponWalletDocument.create(
                request.userId(),
                policy.getId(),
                policy.getKey(),
                issueKey,
                couponIssue.expiresAtFrom(now),
                adminId
            )
        );
        final long issuedCountAfter = couponIssueStockRedisRepository.increment(policy.getId());
        if (couponIssue.isLastIssue(issuedCountAfter)) {
            couponPolicyChangedPublisher.publish(CouponPolicyChangedPublication.ofExhausted(policy));
        }
        return CouponWalletIssueResponse.of(saved);
    }
}

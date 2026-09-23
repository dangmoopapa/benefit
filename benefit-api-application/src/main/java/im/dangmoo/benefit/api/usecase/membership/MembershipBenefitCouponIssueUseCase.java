package im.dangmoo.benefit.api.usecase.membership;

import im.dangmoo.benefit.api.dto.coupon.CouponIssueResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.membership.MembershipBenefitDomain;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyCacheRepository;
import im.dangmoo.benefit.data.entity.coupon.policy.changed.CouponPolicyChangedPublication;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponWalletMongoRepository;
import im.dangmoo.benefit.data.infrastructure.membership.MembershipPolicyMongoRepository;
import im.dangmoo.benefit.data.infrastructure.membership.MembershipContractMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MembershipBenefitCouponIssueUseCase {

    private final MembershipContractMongoRepository membershipContractMongoRepository;
    private final MembershipPolicyMongoRepository membershipPolicyMongoRepository;
    private final CouponPolicyCacheRepository couponPolicyCacheRepository;
    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponIssueStockRedisRepository couponIssueStockRedisRepository;
    private final CouponPolicyChangedPublisher couponPolicyChangedPublisher;

    public MembershipBenefitCouponIssueUseCase(
        final MembershipContractMongoRepository membershipContractMongoRepository,
        final MembershipPolicyMongoRepository membershipPolicyMongoRepository,
        final CouponPolicyCacheRepository couponPolicyCacheRepository,
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponIssueStockRedisRepository couponIssueStockRedisRepository,
        final CouponPolicyChangedPublisher couponPolicyChangedPublisher
    ) {
        this.membershipContractMongoRepository = membershipContractMongoRepository;
        this.membershipPolicyMongoRepository = membershipPolicyMongoRepository;
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponIssueStockRedisRepository = couponIssueStockRedisRepository;
        this.couponPolicyChangedPublisher = couponPolicyChangedPublisher;
    }

    public CouponIssueResponse issue(final String userId) {
        final Instant now = Instant.now();
        final var contract = membershipContractMongoRepository
            .findEffectiveByUserId(userId, now)
            .orElseThrow(ApiException::notFound);

        final var policy = membershipPolicyMongoRepository.findById(contract.getPolicyId())
            .orElseThrow(ApiException::notFound);
        if (!MembershipBenefitDomain.of(policy).isServiceable()) {
            throw ApiException.preparingMembership();
        }

        final String couponPolicyKey = policy.getBenefit().monthlyCouponPolicyKey()
            .orElseThrow(ApiException::conditionNotSatisfied);

        final CouponPolicyCache couponPolicy = couponPolicyCacheRepository.findByKey(couponPolicyKey);
        if (couponPolicy == null) {
            throw ApiException.notFound();
        }

        final CouponIssueDomain couponIssue = CouponIssueDomain.of(couponPolicy);
        final String issueKey = couponIssue.issueKeyFor(couponPolicy.id(), userId, now);
        final boolean alreadyIssued = couponWalletMongoRepository.findByIdempotencyKey(issueKey).isPresent();
        final long issuedCount = couponIssueStockRedisRepository.get(couponPolicy.id());

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
                couponPolicy.id(),
                couponPolicy.key(),
                issueKey,
                couponIssue.expiresAtFrom(now),
                userId
            )
        );
        final long issuedCountAfter = couponIssueStockRedisRepository.increment(couponPolicy.id());
        if (couponIssue.isLastIssue(issuedCountAfter)) {
            couponPolicyChangedPublisher.publish(CouponPolicyChangedPublication.ofExhausted(couponPolicy));
        }
        return CouponIssueResponse.of(saved);
    }
}

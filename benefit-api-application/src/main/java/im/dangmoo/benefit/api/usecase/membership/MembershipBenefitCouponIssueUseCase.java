package im.dangmoo.benefit.api.usecase.membership;

import im.dangmoo.benefit.api.model.coupon.CouponIssueResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.coupon.CouponExhaustionDomain;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponUsageDomain;
import im.dangmoo.benefit.domain.coupon.CouponWalletDomain;
import im.dangmoo.benefit.domain.membership.MembershipBenefitDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CachedCouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCacheRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedEvent;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractMongoRepository;
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
        try {
            MembershipBenefitDomain.requireReady(policy.getSeason(), policy.getBenefit());
        } catch (final MembershipBenefitDomain.PreparingException ex) {
            throw ApiException.preparingMembership();
        }

        final String couponPolicyKey = policy.getBenefit().monthlyCouponPolicyKey()
            .orElseThrow(ApiException::conditionNotSatisfied);

        final CachedCouponPolicy couponPolicy = couponPolicyCacheRepository.findByKey(couponPolicyKey);
        if (couponPolicy == null) {
            throw ApiException.notFound();
        }
        if (couponPolicy.status().isNotActive()) {
            throw ApiException.policyIssueCoupon();
        }

        final String idempotencyKey = CouponWalletDomain.monthlyIdempotencyKey(
            couponPolicy.id(),
            userId,
            now
        );
        if (couponWalletMongoRepository.findByIdempotencyKey(idempotencyKey).isPresent()) {
            throw ApiException.alreadyIssuedCoupon();
        }

        final CouponIssueDomain issueDomain = CouponIssueDomain.of(couponPolicy.issueCondition());
        final CouponExhaustionDomain exhaustionDomain = CouponExhaustionDomain.of(
            couponPolicy.issueCondition()
        );
        final long issuedCount = couponIssueStockRedisRepository.get(couponPolicy.id());
        if (exhaustionDomain.isExhausted(issuedCount)) {
            throw ApiException.stockExhaustedCoupon();
        }
        if (!issueDomain.isSatisfiedAt(now)) {
            throw ApiException.policyIssueCoupon();
        }

        final Instant expiresAt = CouponUsageDomain.of(
            couponPolicy.usageCondition(),
            couponPolicy.applyCondition()
        ).resolveExpiresAt(now);

        final CouponWallet saved = couponWalletMongoRepository.save(
            CouponWallet.create(
                userId,
                couponPolicy.id(),
                couponPolicy.key(),
                idempotencyKey,
                expiresAt,
                userId
            )
        );
        final long issuedCountAfter = couponIssueStockRedisRepository.increment(couponPolicy.id());
        if (exhaustionDomain.isJustExhausted(issuedCountAfter)) {
            couponPolicyChangedPublisher.publish(CouponPolicyChangedEvent.ofExhausted(couponPolicy));
        }
        return CouponIssueResponse.of(saved);
    }
}

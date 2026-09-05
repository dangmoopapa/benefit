package im.dangmoo.benefit.domain.coupon.function.issue;

import im.dangmoo.benefit.domain.coupon.document.policy.CouponPolicy;
import im.dangmoo.benefit.domain.coupon.document.policy.CouponPolicyRepository;
import im.dangmoo.benefit.domain.coupon.document.policy.issue.CouponIssueCondition;
import im.dangmoo.benefit.domain.coupon.document.wallet.CouponWallet;
import im.dangmoo.benefit.domain.coupon.document.wallet.CouponWalletRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class CouponIssuer {

    private final CouponWalletRepository couponWalletRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    public CouponIssuer(
        final CouponWalletRepository couponWalletRepository,
        final CouponPolicyRepository couponPolicyRepository
    ) {
        this.couponWalletRepository = couponWalletRepository;
        this.couponPolicyRepository = couponPolicyRepository;
    }

    public CouponIssueResult issue(
        final String userId,
        final String policyId,
        final boolean enforceIssueCondition,
        final boolean segmentMatched,
        final String actorId
    ) {
        final Optional<CouponPolicy> found = couponPolicyRepository.findById(policyId);
        if (found.isEmpty()) {
            return CouponIssueResult.of(CouponIssueReason.POLICY_NOT_FOUND);
        }

        final CouponPolicy policy = found.get();
        if (!policy.isActive()) {
            return CouponIssueResult.of(CouponIssueReason.POLICY_NOT_ACTIVE);
        }

        final Instant now = Instant.now();
        if (enforceIssueCondition) {
            final CouponIssueCondition issueCondition = policy.getIssueCondition();
            if (!issueCondition.isSatisfiedAt(now, segmentMatched)) {
                return CouponIssueResult.of(CouponIssueReason.ISSUE_NOT_ALLOWED);
            }
            final long issuedCount = couponWalletRepository.countByPolicyId(policy.getId());
            if (!issueCondition.hasRemainingQuantity(issuedCount)) {
                return CouponIssueResult.of(CouponIssueReason.ISSUE_NOT_ALLOWED);
            }
        }

        if (couponWalletRepository.existsByUserIdAndPolicyId(userId, policy.getId())) {
            return CouponIssueResult.of(CouponIssueReason.ALREADY_ISSUED);
        }

        final CouponWallet wallet = couponWalletRepository.save(CouponWallet.create(
            userId,
            policy.getId(),
            policy.getCode(),
            policy.getUsageCondition().getValidity().resolveExpiresAt(now),
            actorId
        ));
        return CouponIssueResult.issued(wallet);
    }
}

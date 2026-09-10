package im.dangmoo.benefit.domain.function.coupon.issue;

import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyRepository;
import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssueCondition;
import im.dangmoo.benefit.domain.data.coupon.stock.CouponStockRepository;
import im.dangmoo.benefit.domain.data.coupon.stock.CouponStockSnapshot;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Component
public class CouponIssuer {

    private final CouponWalletRepository couponWalletRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final CouponStockRepository couponStockRepository;

    public CouponIssuer(
        final CouponWalletRepository couponWalletRepository,
        final CouponPolicyRepository couponPolicyRepository,
        final CouponStockRepository couponStockRepository
    ) {
        this.couponWalletRepository = couponWalletRepository;
        this.couponPolicyRepository = couponPolicyRepository;
        this.couponStockRepository = couponStockRepository;
    }

    public CouponIssueAvailability check(
        final String userId,
        final String policyId,
        final boolean enforceIssueCondition,
        final Collection<String> userSegmentIds
    ) {
        final Optional<CouponPolicy> found = couponPolicyRepository.findById(policyId);
        if (found.isEmpty()) {
            return CouponIssueAvailability.notFound();
        }

        final CouponPolicy policy = found.get();
        final CouponIssueCondition issueCondition = policy.getIssueCondition();
        final Long totalQuantity = enforceIssueCondition ? issueCondition.getTotalQuantity() : null;
        final CouponStockSnapshot stock = couponStockRepository.inspect(policy.getId(), userId, totalQuantity);
        final boolean issueOpen = !enforceIssueCondition
            || issueCondition.isSatisfiedAt(Instant.now(), userSegmentIds);

        if (!policy.isActive()) {
            return CouponIssueAvailability.inactive(issueOpen, stock);
        }
        if (enforceIssueCondition && !issueOpen) {
            return CouponIssueAvailability.closed(stock);
        }
        if (enforceIssueCondition && !stock.remaining()) {
            return CouponIssueAvailability.soldOut(stock);
        }
        if (stock.alreadyIssued()) {
            return CouponIssueAvailability.alreadyIssued(stock);
        }
        return CouponIssueAvailability.issuable(stock);
    }

    public CouponIssue issue(
        final String userId,
        final String policyId,
        final boolean enforceIssueCondition,
        final Collection<String> userSegmentIds,
        final String actorId
    ) {
        final Optional<CouponPolicy> found = couponPolicyRepository.findById(policyId);
        if (found.isEmpty()) {
            return new CouponIssue.PolicyNotFound();
        }

        final CouponPolicy policy = found.get();
        if (!policy.isActive()) {
            return new CouponIssue.PolicyNotActive();
        }

        final Instant now = Instant.now();
        final CouponIssueCondition issueCondition = policy.getIssueCondition();
        if (enforceIssueCondition && !issueCondition.isSatisfiedAt(now, userSegmentIds)) {
            return new CouponIssue.NotAllowed();
        }

        final Long totalQuantity = enforceIssueCondition ? issueCondition.getTotalQuantity() : null;
        return switch (couponStockRepository.reserve(policy.getId(), userId, totalQuantity)) {
            case ALREADY_ISSUED -> new CouponIssue.AlreadyIssued();
            case SOLD_OUT -> new CouponIssue.NotAllowed();
            case RESERVED -> CouponIssue.Success.of(couponWalletRepository.save(CouponWallet.create(
                userId,
                policy.getId(),
                policy.getCode(),
                policy.getUsageCondition().getUsageExpiration().resolveExpiresAt(now),
                actorId
            )));
        };
    }
}

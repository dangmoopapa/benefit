package im.dangmoo.benefit.domain.coupon.function.issue;

import im.dangmoo.benefit.domain.coupon.data.policy.CouponPolicy;
import im.dangmoo.benefit.domain.coupon.data.policy.CouponPolicyRepository;
import im.dangmoo.benefit.domain.coupon.data.policy.issue.CouponIssueCondition;
import im.dangmoo.benefit.domain.coupon.data.stock.CouponStockRepository;
import im.dangmoo.benefit.domain.coupon.data.stock.CouponStockSnapshot;
import im.dangmoo.benefit.domain.coupon.data.wallet.CouponWallet;
import im.dangmoo.benefit.domain.coupon.data.wallet.CouponWalletRepository;
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

    public CouponIssueResult issue(
        final String userId,
        final String policyId,
        final boolean enforceIssueCondition,
        final Collection<String> userSegmentIds,
        final String actorId
    ) {
        final Optional<CouponPolicy> found = couponPolicyRepository.findById(policyId);
        if (found.isEmpty()) {
            return CouponIssueResult.notFound();
        }

        final CouponPolicy policy = found.get();
        if (!policy.isActive()) {
            return CouponIssueResult.inactive();
        }

        final Instant now = Instant.now();
        final CouponIssueCondition issueCondition = policy.getIssueCondition();
        if (enforceIssueCondition && !issueCondition.isSatisfiedAt(now, userSegmentIds)) {
            return CouponIssueResult.closed();
        }

        final Long totalQuantity = enforceIssueCondition ? issueCondition.getTotalQuantity() : null;
        return switch (couponStockRepository.reserve(policy.getId(), userId, totalQuantity)) {
            case ALREADY_ISSUED -> CouponIssueResult.alreadyIssued();
            case SOLD_OUT -> CouponIssueResult.soldOut();
            case RESERVED -> CouponIssueResult.issued(couponWalletRepository.save(CouponWallet.create(
                userId,
                policy.getId(),
                policy.getCode(),
                policy.getUsageCondition().getValidity().resolveExpiresAt(now),
                actorId
            )));
        };
    }
}

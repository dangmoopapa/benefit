package im.dangmoo.benefit.domain.component.coupon.issue;

import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssueCondition;
import im.dangmoo.benefit.domain.data.coupon.stock.CouponStockSnapshot;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyRepository;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletRepository;
import im.dangmoo.benefit.domain.data.coupon.stock.CouponStockRepository;
import im.dangmoo.benefit.domain.util.TimeUtils;
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
        final Instant now = TimeUtils.now();
        final String idempotencyKey = policy.issueIdempotencyKey(userId, now);
        final Long totalQuantity = enforceIssueCondition && issueCondition != null ? issueCondition.getTotalQuantity() : null;
        final CouponStockSnapshot stock = inspectStock(policy.getId(), idempotencyKey, totalQuantity);
        final boolean issueOpen = !enforceIssueCondition
            || (issueCondition != null && issueCondition.isSatisfiedAt(now, userSegmentIds));

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

    public CouponIssue grantByCode(final String userId, final String policyCode, final String actorId) {
        final Optional<CouponPolicy> found = couponPolicyRepository.findByCode(policyCode);
        if (found.isEmpty()) {
            return new CouponIssue.PolicyNotFound();
        }
        final CouponPolicy policy = found.get();
        if (!policy.isActive()) {
            return new CouponIssue.PolicyNotActive();
        }
        final Instant now = TimeUtils.now();
        final CouponIssueCondition issueCondition = policy.getIssueCondition();
        return grant(
            policy,
            userId,
            now,
            issueCondition == null ? null : issueCondition.getTotalQuantity(),
            actorId
        );
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

        final Instant now = TimeUtils.now();
        final CouponIssueCondition issueCondition = policy.getIssueCondition();
        if (enforceIssueCondition && (issueCondition == null || !issueCondition.isSatisfiedAt(now, userSegmentIds))) {
            return new CouponIssue.NotAllowed();
        }

        final Long totalQuantity = enforceIssueCondition ? issueCondition.getTotalQuantity() : null;
        return grant(policy, userId, now, totalQuantity, actorId);
    }

    private CouponIssue grant(
        final CouponPolicy policy,
        final String userId,
        final Instant now,
        final Long totalQuantity,
        final String actorId
    ) {
        final String idempotencyKey = policy.issueIdempotencyKey(userId, now);
        return switch (couponStockRepository.reserve(policy.getId(), idempotencyKey, totalQuantity)) {
            case ALREADY_ISSUED -> new CouponIssue.AlreadyIssued();
            case SOLD_OUT -> new CouponIssue.NotAllowed();
            case RESERVED -> CouponIssue.Success.of(couponWalletRepository.save(CouponWallet.create(
                userId,
                policy.getId(),
                policy.getCode(),
                idempotencyKey,
                policy.getUsageCondition().getUsageExpiration().resolveExpiresAt(now),
                actorId
            )));
        };
    }

    private CouponStockSnapshot inspectStock(
        final String policyId,
        final String idempotencyKey,
        final Long totalQuantity
    ) {
        if (totalQuantity != null) {
            final long issuedCount = couponStockRepository.getIssuedCount(policyId);
            if (issuedCount >= totalQuantity) {
                return CouponStockSnapshot.soldOut(issuedCount, totalQuantity);
            }
            if (couponStockRepository.hasIssued(policyId, idempotencyKey)) {
                return CouponStockSnapshot.alreadyIssued(issuedCount, totalQuantity);
            }
            return CouponStockSnapshot.available(issuedCount, totalQuantity);
        }

        if (couponStockRepository.hasIssued(policyId, idempotencyKey)) {
            return CouponStockSnapshot.alreadyIssued(0L, null);
        }
        return CouponStockSnapshot.available(0L, null);
    }
}

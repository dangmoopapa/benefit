package im.dangmoo.benefit.domain.component.point.issue;

import im.dangmoo.benefit.domain.component.point.balance.PointBalanceIssuer;
import im.dangmoo.benefit.domain.data.point.policy.PointPolicy;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransaction;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionType;
import im.dangmoo.benefit.domain.data.point.policy.PointPolicyRepository;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionRepository;
import im.dangmoo.benefit.domain.util.TimeUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class PointIssuer {

    private final PointPolicyRepository pointPolicyRepository;
    private final PointBalanceIssuer pointBalanceIssuer;
    private final PointTransactionRepository pointTransactionRepository;

    public PointIssuer(
        final PointPolicyRepository pointPolicyRepository,
        final PointBalanceIssuer pointBalanceIssuer,
        final PointTransactionRepository pointTransactionRepository
    ) {
        this.pointPolicyRepository = pointPolicyRepository;
        this.pointBalanceIssuer = pointBalanceIssuer;
        this.pointTransactionRepository = pointTransactionRepository;
    }

    public PointIssue issue(
        final String userId,
        final String policyCode,
        final long point,
        final String orderId,
        final String actorId
    ) {
        if (point <= 0) {
            return new PointIssue.InvalidAmount();
        }

        final Optional<PointPolicy> found = pointPolicyRepository.findByCode(policyCode);
        if (found.isEmpty()) {
            return new PointIssue.PolicyNotFound();
        }
        final PointPolicy policy = found.get();
        if (!policy.isActive()) {
            return new PointIssue.PolicyNotActive();
        }

        final Instant now = TimeUtils.now();
        final String idempotencyKey = policy.issueIdempotencyKey(userId, now);
        final Optional<PointTransaction> replay = pointTransactionRepository.findByIdempotencyKey(idempotencyKey);
        if (replay.isPresent()) {
            return PointIssue.Success.of(replay.get(), true);
        }

        final Instant expiresAt = policy.resolveExpiresAt(now);
        final Optional<PointTransaction> inserted = pointTransactionRepository.tryInsert(PointTransaction.issued(
            userId, point, orderId, idempotencyKey, policy.getId(), expiresAt, actorId, now
        ));
        if (inserted.isEmpty()) {
            return PointIssue.Success.of(
                pointTransactionRepository.findByIdempotencyKey(idempotencyKey).orElseThrow(),
                true
            );
        }

        pointBalanceIssuer.issue(userId, point, expiresAt, actorId, now);
        return PointIssue.Success.of(inserted.get(), false);
    }

    public PointRevoke revoke(
        final String relatedTransactionId,
        final String orderId,
        final String idempotencyKey,
        final String expectedUserId,
        final String actorId
    ) {
        final Optional<PointTransaction> replay = pointTransactionRepository.findByIdempotencyKey(idempotencyKey);
        if (replay.isPresent()) {
            return PointRevoke.Success.of(replay.get(), true);
        }

        final Optional<PointTransaction> found = pointTransactionRepository.findById(relatedTransactionId);
        if (found.isEmpty()) {
            return new PointRevoke.TransactionNotFound();
        }
        final PointTransaction issued = found.get();
        if (issued.getType() != PointTransactionType.ISSUED) {
            return new PointRevoke.InvalidType();
        }
        if (expectedUserId != null && !expectedUserId.equals(issued.getUserId())) {
            return new PointRevoke.UserMismatch();
        }
        if (pointTransactionRepository.existsByRelatedTransactionId(issued.getId())) {
            return new PointRevoke.AlreadyRevoked();
        }

        final Instant now = TimeUtils.now();
        final Optional<PointTransaction> inserted = pointTransactionRepository.tryInsert(PointTransaction.revoked(
            issued.getUserId(),
            issued.getPoint(),
            orderId,
            idempotencyKey,
            issued.getId(),
            issued.getPolicyId(),
            issued.getExpiresAt(),
            actorId,
            now
        ));
        if (inserted.isEmpty()) {
            return pointTransactionRepository.findByIdempotencyKey(idempotencyKey)
                .<PointRevoke>map(tx -> PointRevoke.Success.of(tx, true))
                .orElseGet(PointRevoke.AlreadyRevoked::new);
        }

        try {
            pointBalanceIssuer.revoke(issued.getUserId(), issued.getPoint(), issued.getExpiresAt(), actorId, now);
        } catch (final IllegalStateException ex) {
            return new PointRevoke.Insufficient();
        }
        return PointRevoke.Success.of(inserted.get(), false);
    }
}

package im.dangmoo.benefit.domain.component.point.use;

import im.dangmoo.benefit.domain.component.point.balance.PointBalanceSpender;
import im.dangmoo.benefit.domain.data.point.balance.PointLot;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransaction;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionType;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionRepository;
import im.dangmoo.benefit.domain.util.TimeUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class PointRedeemer {

    private final PointBalanceSpender pointBalanceSpender;
    private final PointTransactionRepository pointTransactionRepository;

    public PointRedeemer(
        final PointBalanceSpender pointBalanceSpender,
        final PointTransactionRepository pointTransactionRepository
    ) {
        this.pointBalanceSpender = pointBalanceSpender;
        this.pointTransactionRepository = pointTransactionRepository;
    }

    public PointUse use(
        final String userId,
        final long point,
        final String orderId,
        final String idempotencyKey,
        final String actorId
    ) {
        if (point <= 0) {
            return new PointUse.InvalidAmount();
        }
        final Optional<PointTransaction> replay = pointTransactionRepository.findByIdempotencyKey(idempotencyKey);
        if (replay.isPresent()) {
            return PointUse.Success.of(replay.get(), true);
        }

        final Instant now = TimeUtils.now();
        final List<PointLot> usedLots;
        try {
            usedLots = pointBalanceSpender.spend(userId, point, actorId, now);
        } catch (final IllegalStateException ex) {
            return new PointUse.Insufficient();
        }

        final Optional<PointTransaction> inserted = pointTransactionRepository.tryInsert(PointTransaction.used(
            userId, point, orderId, idempotencyKey, usedLots, actorId, now
        ));
        if (inserted.isEmpty()) {
            pointBalanceSpender.restore(userId, usedLots, actorId, now);
            return PointUse.Success.of(
                pointTransactionRepository.findByIdempotencyKey(idempotencyKey).orElseThrow(),
                true
            );
        }
        return PointUse.Success.of(inserted.get(), false);
    }

    public PointRestore restore(
        final String relatedTransactionId,
        final String orderId,
        final String idempotencyKey,
        final String expectedUserId,
        final String actorId
    ) {
        final Optional<PointTransaction> replay = pointTransactionRepository.findByIdempotencyKey(idempotencyKey);
        if (replay.isPresent()) {
            return PointRestore.Success.of(replay.get(), true);
        }

        final Optional<PointTransaction> found = pointTransactionRepository.findById(relatedTransactionId);
        if (found.isEmpty()) {
            return new PointRestore.TransactionNotFound();
        }
        final PointTransaction used = found.get();
        if (used.getType() != PointTransactionType.USED) {
            return new PointRestore.InvalidType();
        }
        if (expectedUserId != null && !expectedUserId.equals(used.getUserId())) {
            return new PointRestore.UserMismatch();
        }
        if (pointTransactionRepository.existsByRelatedTransactionId(used.getId())) {
            return new PointRestore.AlreadyRestored();
        }

        final Instant now = TimeUtils.now();
        final Optional<PointTransaction> inserted = pointTransactionRepository.tryInsert(PointTransaction.restored(
            used.getUserId(),
            used.getPoint(),
            orderId,
            idempotencyKey,
            used.getId(),
            used.getUsedLots(),
            actorId,
            now
        ));
        if (inserted.isEmpty()) {
            return pointTransactionRepository.findByIdempotencyKey(idempotencyKey)
                .<PointRestore>map(tx -> PointRestore.Success.of(tx, true))
                .orElseGet(PointRestore.AlreadyRestored::new);
        }

        pointBalanceSpender.restore(used.getUserId(), used.getUsedLots(), actorId, now);
        return PointRestore.Success.of(inserted.get(), false);
    }
}

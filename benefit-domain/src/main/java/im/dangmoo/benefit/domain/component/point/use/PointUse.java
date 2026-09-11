package im.dangmoo.benefit.domain.component.point.use;

import im.dangmoo.benefit.domain.data.point.balance.PointLot;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransaction;

import java.time.Instant;
import java.util.List;

public sealed interface PointUse {

    record Lot(Instant expiresAt, long point) {
        static Lot of(final PointLot lot) {
            return new Lot(lot.getExpiresAt(), lot.getPoint());
        }
    }

    record Success(
        String transactionId,
        String userId,
        long point,
        String orderId,
        String idempotencyKey,
        Instant transactionAt,
        List<Lot> usedLots,
        String createdBy,
        Instant createdAt,
        boolean replayed
    ) implements PointUse {
        static Success of(final PointTransaction transaction, final boolean replayed) {
            return new Success(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getPoint(),
                transaction.getOrderId(),
                transaction.getIdempotencyKey(),
                transaction.getTransactionAt(),
                transaction.getUsedLots().stream().map(Lot::of).toList(),
                transaction.getCreatedBy(),
                transaction.getCreatedAt(),
                replayed
            );
        }
    }

    record Insufficient() implements PointUse {
    }

    record InvalidAmount() implements PointUse {
    }
}

package im.dangmoo.benefit.domain.component.point.use;

import im.dangmoo.benefit.domain.data.point.transaction.PointTransaction;

import java.time.Instant;

public sealed interface PointRestore {

    record Success(
        String transactionId,
        String userId,
        long point,
        String orderId,
        String idempotencyKey,
        String relatedTransactionId,
        Instant transactionAt,
        String createdBy,
        Instant createdAt,
        boolean replayed
    ) implements PointRestore {
        static Success of(final PointTransaction transaction, final boolean replayed) {
            return new Success(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getPoint(),
                transaction.getOrderId(),
                transaction.getIdempotencyKey(),
                transaction.getRelatedTransactionId(),
                transaction.getTransactionAt(),
                transaction.getCreatedBy(),
                transaction.getCreatedAt(),
                replayed
            );
        }
    }

    record TransactionNotFound() implements PointRestore {
    }

    record InvalidType() implements PointRestore {
    }

    record UserMismatch() implements PointRestore {
    }

    record AlreadyRestored() implements PointRestore {
    }
}

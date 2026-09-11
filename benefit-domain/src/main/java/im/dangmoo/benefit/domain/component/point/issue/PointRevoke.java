package im.dangmoo.benefit.domain.component.point.issue;

import im.dangmoo.benefit.domain.data.point.transaction.PointTransaction;

import java.time.Instant;

public sealed interface PointRevoke {

    record Success(
        String transactionId,
        String userId,
        long point,
        String orderId,
        String idempotencyKey,
        String relatedTransactionId,
        String policyId,
        Instant expiresAt,
        Instant transactionAt,
        String createdBy,
        Instant createdAt,
        boolean replayed
    ) implements PointRevoke {
        static Success of(final PointTransaction transaction, final boolean replayed) {
            return new Success(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getPoint(),
                transaction.getOrderId(),
                transaction.getIdempotencyKey(),
                transaction.getRelatedTransactionId(),
                transaction.getPolicyId(),
                transaction.getExpiresAt(),
                transaction.getTransactionAt(),
                transaction.getCreatedBy(),
                transaction.getCreatedAt(),
                replayed
            );
        }
    }

    record TransactionNotFound() implements PointRevoke {
    }

    record InvalidType() implements PointRevoke {
    }

    record UserMismatch() implements PointRevoke {
    }

    record AlreadyRevoked() implements PointRevoke {
    }

    record Insufficient() implements PointRevoke {
    }
}

package im.dangmoo.benefit.domain.function.point.issue;

import im.dangmoo.benefit.domain.data.point.transaction.PointTransaction;

import java.time.Instant;

public sealed interface PointIssue {

    record Success(
        String transactionId,
        String userId,
        long point,
        String orderId,
        String idempotencyKey,
        String policyId,
        Instant expiresAt,
        Instant transactionAt,
        String createdBy,
        Instant createdAt,
        boolean replayed
    ) implements PointIssue {
        static Success of(final PointTransaction transaction, final boolean replayed) {
            return new Success(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getPoint(),
                transaction.getOrderId(),
                transaction.getIdempotencyKey(),
                transaction.getPolicyId(),
                transaction.getExpiresAt(),
                transaction.getTransactionAt(),
                transaction.getCreatedBy(),
                transaction.getCreatedAt(),
                replayed
            );
        }
    }

    record PolicyNotFound() implements PointIssue {
    }

    record PolicyNotActive() implements PointIssue {
    }

    record InvalidAmount() implements PointIssue {
    }
}

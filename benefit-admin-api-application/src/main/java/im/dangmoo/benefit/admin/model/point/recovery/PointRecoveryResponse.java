package im.dangmoo.benefit.admin.model.point.recovery;

import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionDocument;

import java.time.Instant;

public record PointRecoveryResponse(
    String transactionId,
    String userId,
    String orderId,
    String originalTransactionId,
    long amount,
    Instant transactionAt
) {

    public static PointRecoveryResponse of(final PointTransactionDocument transaction) {
        return new PointRecoveryResponse(
            transaction.getId(),
            transaction.getUserId(),
            transaction.getOrderId(),
            transaction.getOriginalTransactionId(),
            transaction.getAmount(),
            transaction.getTransactionAt()
        );
    }
}

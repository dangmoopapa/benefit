package im.dangmoo.benefit.api.dto.point;

import im.dangmoo.benefit.data.entity.point.transaction.PointTransactionDocument;

import java.time.Instant;

public record PointRecoveryResponse(
    String transactionId,
    String orderId,
    String originalTransactionId,
    long amount,
    Instant transactionAt
) {

    public static PointRecoveryResponse of(final PointTransactionDocument transaction) {
        return new PointRecoveryResponse(
            transaction.getId(),
            transaction.getOrderId(),
            transaction.getOriginalTransactionId(),
            transaction.getAmount(),
            transaction.getTransactionAt()
        );
    }
}

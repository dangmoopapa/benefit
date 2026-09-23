package im.dangmoo.benefit.api.dto.point;

import im.dangmoo.benefit.data.entity.point.transaction.PointTransactionDocument;

import java.time.Instant;

public record PointUsageResponse(
    String transactionId,
    long amount,
    String orderId,
    Instant transactionAt
) {

    public static PointUsageResponse of(final PointTransactionDocument transaction) {
        return new PointUsageResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getOrderId(),
            transaction.getTransactionAt()
        );
    }
}

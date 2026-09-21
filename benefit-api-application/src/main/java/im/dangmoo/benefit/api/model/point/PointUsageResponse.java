package im.dangmoo.benefit.api.model.point;

import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;

import java.time.Instant;

public record PointUsageResponse(
    String transactionId,
    long amount,
    String orderId,
    Instant transactionAt
) {

    public static PointUsageResponse of(final PointTransaction transaction) {
        return new PointUsageResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getOrderId(),
            transaction.getTransactionAt()
        );
    }
}

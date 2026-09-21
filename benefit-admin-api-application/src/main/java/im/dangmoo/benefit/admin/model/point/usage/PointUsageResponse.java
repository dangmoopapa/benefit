package im.dangmoo.benefit.admin.model.point.usage;

import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;

import java.time.Instant;

public record PointUsageResponse(
    String transactionId,
    String userId,
    long amount,
    String orderId,
    Instant transactionAt
) {

    public static PointUsageResponse of(final PointTransaction transaction) {
        return new PointUsageResponse(
            transaction.getId(),
            transaction.getUserId(),
            transaction.getAmount(),
            transaction.getOrderId(),
            transaction.getTransactionAt()
        );
    }
}

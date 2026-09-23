package im.dangmoo.benefit.api.model.point;

import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionDocument;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionType;

import java.time.Instant;
import java.util.List;

public record PointTransactionListResponse(
    List<Item> items,
    long totalElements,
    int totalPages,
    boolean hasNext,
    int page,
    int size
) {

    public static PointTransactionListResponse of(
        final List<PointTransactionDocument> transactions,
        final long totalElements,
        final int page,
        final int size
    ) {
        final int totalPages = size == 0 ? 0 : (int) ((totalElements + size - 1) / size);
        return new PointTransactionListResponse(
            transactions.stream().map(Item::of).toList(),
            totalElements,
            totalPages,
            page + 1 < totalPages,
            page,
            size
        );
    }

    public record Item(
        String id,
        PointTransactionType type,
        long amount,
        String policyKey,
        Instant expiresAt,
        boolean neverExpires,
        String orderId,
        String originalTransactionId,
        Instant transactionAt
    ) {

        public static Item of(final PointTransactionDocument transaction) {
            return new Item(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getPolicyKey(),
                PointExpireDomain.of(transaction.getExpiresAt()).expiresAtOrNull(),
                PointExpireDomain.of(transaction.getExpiresAt()).neverExpires(),
                transaction.getOrderId(),
                transaction.getOriginalTransactionId(),
                transaction.getTransactionAt()
            );
        }
    }
}

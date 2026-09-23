package im.dangmoo.benefit.admin.model.point.grant;

import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionDocument;

import java.time.Instant;

public record PointGrantResponse(
    String transactionId,
    String userId,
    String policyId,
    String policyKey,
    long amount,
    Instant expiresAt,
    boolean neverExpires,
    Instant transactionAt
) {

    public static PointGrantResponse of(final PointTransactionDocument transaction) {
        return new PointGrantResponse(
            transaction.getId(),
            transaction.getUserId(),
            transaction.getPolicyId(),
            transaction.getPolicyKey(),
            transaction.getAmount(),
            PointExpireDomain.of(transaction.getExpiresAt()).expiresAtOrNull(),
            PointExpireDomain.of(transaction.getExpiresAt()).neverExpires(),
            transaction.getTransactionAt()
        );
    }
}

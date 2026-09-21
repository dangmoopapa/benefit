package im.dangmoo.benefit.api.model.point;

import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;

import java.time.Instant;

public record PointGrantResponse(
    String transactionId,
    String policyId,
    String policyKey,
    long amount,
    Instant expiresAt,
    boolean neverExpires,
    Instant transactionAt
) {

    public static PointGrantResponse of(final PointTransaction transaction) {
        return new PointGrantResponse(
            transaction.getId(),
            transaction.getPolicyId(),
            transaction.getPolicyKey(),
            transaction.getAmount(),
            PointExpireDomain.toClientExpiresAt(transaction.getExpiresAt()),
            PointExpireDomain.isNever(transaction.getExpiresAt()),
            transaction.getTransactionAt()
        );
    }
}

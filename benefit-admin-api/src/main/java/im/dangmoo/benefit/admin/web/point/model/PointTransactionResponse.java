package im.dangmoo.benefit.admin.web.point.model;

import im.dangmoo.benefit.domain.data.point.transaction.PointTransaction;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionType;
import im.dangmoo.benefit.domain.component.point.issue.PointIssue;
import im.dangmoo.benefit.domain.component.point.issue.PointRevoke;
import im.dangmoo.benefit.domain.component.point.use.PointRestore;
import im.dangmoo.benefit.domain.component.point.use.PointUse;

import java.time.Instant;
import java.util.List;

public record PointTransactionResponse(
    String id,
    String userId,
    PointTransactionType type,
    long point,
    String orderId,
    String idempotencyKey,
    Instant transactionAt,
    String policyId,
    Instant expiresAt,
    String relatedTransactionId,
    List<PointLotResponse> usedLots,
    String createdBy,
    Instant createdAt,
    boolean replayed
) {

    public static PointTransactionResponse of(final PointTransaction transaction) {
        return new PointTransactionResponse(
            transaction.getId(),
            transaction.getUserId(),
            transaction.getType(),
            transaction.getPoint(),
            transaction.getOrderId(),
            transaction.getIdempotencyKey(),
            transaction.getTransactionAt(),
            transaction.getPolicyId(),
            transaction.getExpiresAt(),
            transaction.getRelatedTransactionId(),
            transaction.getUsedLots().stream()
                .map(lot -> new PointLotResponse(lot.getExpiresAt(), lot.getPoint()))
                .toList(),
            transaction.getCreatedBy(),
            transaction.getCreatedAt(),
            false
        );
    }

    public static PointTransactionResponse of(final PointIssue.Success issued) {
        return new PointTransactionResponse(
            issued.transactionId(),
            issued.userId(),
            PointTransactionType.ISSUED,
            issued.point(),
            issued.orderId(),
            issued.idempotencyKey(),
            issued.transactionAt(),
            issued.policyId(),
            issued.expiresAt(),
            null,
            List.of(),
            issued.createdBy(),
            issued.createdAt(),
            issued.replayed()
        );
    }

    public static PointTransactionResponse of(final PointRevoke.Success revoked) {
        return new PointTransactionResponse(
            revoked.transactionId(),
            revoked.userId(),
            PointTransactionType.REVOKED,
            revoked.point(),
            revoked.orderId(),
            revoked.idempotencyKey(),
            revoked.transactionAt(),
            revoked.policyId(),
            revoked.expiresAt(),
            revoked.relatedTransactionId(),
            List.of(),
            revoked.createdBy(),
            revoked.createdAt(),
            revoked.replayed()
        );
    }

    public static PointTransactionResponse of(final PointUse.Success used) {
        return new PointTransactionResponse(
            used.transactionId(),
            used.userId(),
            PointTransactionType.USED,
            used.point(),
            used.orderId(),
            used.idempotencyKey(),
            used.transactionAt(),
            null,
            null,
            null,
            used.usedLots().stream()
                .map(lot -> new PointLotResponse(lot.expiresAt(), lot.point()))
                .toList(),
            used.createdBy(),
            used.createdAt(),
            used.replayed()
        );
    }

    public static PointTransactionResponse of(final PointRestore.Success restored) {
        return new PointTransactionResponse(
            restored.transactionId(),
            restored.userId(),
            PointTransactionType.RESTORED,
            restored.point(),
            restored.orderId(),
            restored.idempotencyKey(),
            restored.transactionAt(),
            null,
            null,
            restored.relatedTransactionId(),
            List.of(),
            restored.createdBy(),
            restored.createdAt(),
            restored.replayed()
        );
    }

    public record PointLotResponse(Instant expiresAt, long point) {
    }
}

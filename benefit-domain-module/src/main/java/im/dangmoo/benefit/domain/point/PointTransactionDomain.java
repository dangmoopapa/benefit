package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionType;

import java.time.Instant;

public final class PointTransactionDomain {

    private PointTransactionDomain() {
    }

    public static String grantKey(final String policyId, final String userId) {
        return PointTransactionType.GRANT.name() + ":" + policyId + ":" + userId;
    }

    public static String reclaimKey(final String idempotencyKey) {
        return PointTransactionType.RECLAIM.name() + ":" + idempotencyKey;
    }

    public static String useKey(final String orderId) {
        return PointTransactionType.USE.name() + ":" + orderId;
    }

    public static String useCancelKey(final String orderId) {
        return PointTransactionType.USE_CANCEL.name() + ":" + orderId;
    }

    public static String expireKey(final String userId, final Instant expiresAt) {
        return PointTransactionType.EXPIRE.name() + ":" + userId + ":" + expiresAt;
    }
}

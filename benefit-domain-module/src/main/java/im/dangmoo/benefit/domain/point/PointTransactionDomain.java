package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionType;

public final class PointTransactionDomain {

    private PointTransactionDomain() {
    }

    public static String grantKey(final String policyId, final String userId) {
        return PointTransactionType.GRANT.name() + ":" + policyId + ":" + userId;
    }

    public static String useKey(final String orderId) {
        return PointTransactionType.USE.name() + ":" + orderId;
    }
}

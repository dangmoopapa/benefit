package im.dangmoo.benefit.admin.model.point.reclaim;

import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;

public record PointReclaimResponse(
    String transactionId,
    String userId,
    String policyId,
    String policyKey,
    long amount
) {

    public static PointReclaimResponse of(final PointTransaction tx) {
        return new PointReclaimResponse(
            tx.getId(),
            tx.getUserId(),
            tx.getPolicyId(),
            tx.getPolicyKey(),
            tx.getAmount()
        );
    }
}

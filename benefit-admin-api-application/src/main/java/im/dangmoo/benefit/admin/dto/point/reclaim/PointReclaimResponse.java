package im.dangmoo.benefit.admin.dto.point.reclaim;

import im.dangmoo.benefit.data.entity.point.transaction.PointTransactionDocument;

public record PointReclaimResponse(
    String transactionId,
    String userId,
    String policyId,
    String policyKey,
    long amount
) {

    public static PointReclaimResponse of(final PointTransactionDocument tx) {
        return new PointReclaimResponse(
            tx.getId(),
            tx.getUserId(),
            tx.getPolicyId(),
            tx.getPolicyKey(),
            tx.getAmount()
        );
    }
}

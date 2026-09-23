package im.dangmoo.benefit.api.dto.point;

import im.dangmoo.benefit.data.entity.point.transaction.PointTransactionDocument;

public record PointReclaimResponse(
    String transactionId,
    String policyId,
    String policyKey,
    long amount
) {

    public static PointReclaimResponse of(final PointTransactionDocument tx) {
        return new PointReclaimResponse(
            tx.getId(),
            tx.getPolicyId(),
            tx.getPolicyKey(),
            tx.getAmount()
        );
    }
}

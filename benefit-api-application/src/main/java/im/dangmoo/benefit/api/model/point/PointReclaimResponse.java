package im.dangmoo.benefit.api.model.point;

import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;

public record PointReclaimResponse(
    String transactionId,
    String policyId,
    String policyKey,
    long amount
) {

    public static PointReclaimResponse of(final PointTransaction tx) {
        return new PointReclaimResponse(
            tx.getId(),
            tx.getPolicyId(),
            tx.getPolicyKey(),
            tx.getAmount()
        );
    }
}

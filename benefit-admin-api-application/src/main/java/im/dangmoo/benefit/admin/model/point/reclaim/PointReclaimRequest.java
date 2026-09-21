package im.dangmoo.benefit.admin.model.point.reclaim;

public record PointReclaimRequest(
    String policyKey,
    String userId,
    Long amount,
    String idempotencyKey
) {
}

package im.dangmoo.benefit.admin.dto.point.reclaim;

public record PointReclaimRequest(
    String policyKey,
    String userId,
    Long amount,
    String idempotencyKey
) {
}

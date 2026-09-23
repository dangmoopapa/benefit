package im.dangmoo.benefit.api.dto.point;

public record PointReclaimRequest(
    String policyKey,
    Long amount,
    String idempotencyKey
) {
}

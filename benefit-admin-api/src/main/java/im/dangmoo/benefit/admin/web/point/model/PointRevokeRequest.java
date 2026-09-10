package im.dangmoo.benefit.admin.web.point.model;

public record PointRevokeRequest(
    String relatedTransactionId,
    String orderId,
    String idempotencyKey
) {
}

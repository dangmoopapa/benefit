package im.dangmoo.benefit.api.web.point.model;

public record PointRevokeRequest(
    String relatedTransactionId,
    String orderId,
    String idempotencyKey
) {
}

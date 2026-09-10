package im.dangmoo.benefit.api.web.point.model;

public record PointRestoreRequest(
    String relatedTransactionId,
    String orderId,
    String idempotencyKey
) {
}

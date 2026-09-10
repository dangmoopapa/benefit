package im.dangmoo.benefit.admin.web.point.model;

public record PointRestoreRequest(
    String relatedTransactionId,
    String orderId,
    String idempotencyKey
) {
}

package im.dangmoo.benefit.admin.web.point.model;

public record PointUseRequest(
    String userId,
    long point,
    String orderId,
    String idempotencyKey
) {
}

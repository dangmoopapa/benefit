package im.dangmoo.benefit.api.web.point.model;

public record PointUseRequest(
    long point,
    String orderId,
    String idempotencyKey
) {
}

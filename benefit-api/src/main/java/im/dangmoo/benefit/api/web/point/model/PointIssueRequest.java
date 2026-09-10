package im.dangmoo.benefit.api.web.point.model;

public record PointIssueRequest(
    String policyCode,
    long point,
    String orderId,
    String idempotencyKey
) {
}

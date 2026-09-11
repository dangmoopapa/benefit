package im.dangmoo.benefit.admin.web.point.model;

public record PointIssueRequest(
    String userId,
    String policyCode,
    long point,
    String orderId
) {
}

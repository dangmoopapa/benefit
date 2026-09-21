package im.dangmoo.benefit.admin.model.point.grant;

public record PointGrantRequest(
    String policyKey,
    String userId
) {
}

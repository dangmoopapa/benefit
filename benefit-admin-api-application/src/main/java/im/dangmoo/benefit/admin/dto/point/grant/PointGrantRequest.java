package im.dangmoo.benefit.admin.dto.point.grant;

public record PointGrantRequest(
    String policyKey,
    String userId
) {
}

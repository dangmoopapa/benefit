package im.dangmoo.benefit.admin.dto.point.recovery;

public record PointRecoveryRequest(
    String userId,
    String orderId
) {
}

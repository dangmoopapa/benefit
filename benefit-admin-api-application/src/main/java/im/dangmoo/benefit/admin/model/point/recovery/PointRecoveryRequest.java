package im.dangmoo.benefit.admin.model.point.recovery;

public record PointRecoveryRequest(
    String userId,
    String orderId
) {
}

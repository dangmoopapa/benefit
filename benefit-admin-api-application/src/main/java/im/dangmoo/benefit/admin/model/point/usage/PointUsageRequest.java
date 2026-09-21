package im.dangmoo.benefit.admin.model.point.usage;

public record PointUsageRequest(
    String userId,
    long amount,
    String orderId
) {
}

package im.dangmoo.benefit.admin.dto.point.usage;

public record PointUsageRequest(
    String userId,
    long amount,
    String orderId
) {
}

package im.dangmoo.benefit.api.dto.point;

public record PointUsageRequest(
    long amount,
    String orderId
) {
}

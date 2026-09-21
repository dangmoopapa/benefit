package im.dangmoo.benefit.api.model.point;

public record PointUsageRequest(
    long amount,
    String orderId
) {
}

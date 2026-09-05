package im.dangmoo.benefit.domain.coupon.data.stock;

public record CouponStockResult(
    Status status,
    boolean alreadyIssued,
    long issuedCount,
    Long totalQuantity,
    boolean remaining
) {

    public enum Status {
        OK,
        ALREADY_ISSUED,
        SOLD_OUT
    }

    static CouponStockResult inspected(final String reply, final Long totalQuantity) {
        final String[] parts = reply.split("\\|", 3);
        final boolean alreadyIssued = "1".equals(parts[0]);
        final long issuedCount = Long.parseLong(parts[1]);
        final boolean remaining = totalQuantity == null || Long.parseLong(parts[2]) > 0L;
        final Status status = alreadyIssued
            ? Status.ALREADY_ISSUED
            : remaining ? Status.OK : Status.SOLD_OUT;
        return new CouponStockResult(status, alreadyIssued, issuedCount, totalQuantity, remaining);
    }

    static CouponStockResult reserved(final String reply, final Long totalQuantity) {
        final String[] parts = reply.split("\\|", 2);
        final Status status = Status.valueOf(parts[0]);
        final long issuedCount = Long.parseLong(parts[1]);
        return new CouponStockResult(
            status,
            status == Status.ALREADY_ISSUED,
            issuedCount,
            totalQuantity,
            status != Status.SOLD_OUT
        );
    }
}

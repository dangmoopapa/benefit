package im.dangmoo.benefit.domain.data.coupon.stock;

/**
 * inspect 결과. 발급 가능 여부 판단·응답용 스냅샷.
 */
public record CouponStockSnapshot(
    boolean alreadyIssued,
    boolean remaining,
    long issuedCount,
    Long totalQuantity
) {

    public static CouponStockSnapshot soldOut(final long issuedCount, final Long totalQuantity) {
        return new CouponStockSnapshot(false, false, issuedCount, totalQuantity);
    }

    public static CouponStockSnapshot alreadyIssued(final long issuedCount, final Long totalQuantity) {
        return new CouponStockSnapshot(true, true, issuedCount, totalQuantity);
    }

    public static CouponStockSnapshot available(final long issuedCount, final Long totalQuantity) {
        return new CouponStockSnapshot(false, true, issuedCount, totalQuantity);
    }
}

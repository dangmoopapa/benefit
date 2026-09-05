package im.dangmoo.benefit.domain.function.coupon.issue;

import im.dangmoo.benefit.domain.data.coupon.stock.CouponStockSnapshot;

public record CouponIssueAvailability(
    boolean issuable,
    CouponIssueReason reason,
    boolean policyActive,
    boolean issueOpen,
    boolean quantityRemaining,
    boolean alreadyIssued,
    Long totalQuantity,
    long issuedCount
) {

    private static final long EMPTY_ISSUED_COUNT = 0L;

    public static CouponIssueAvailability notFound() {
        return create(CouponIssueReason.POLICY_NOT_FOUND, false, false, false, false, null, EMPTY_ISSUED_COUNT);
    }

    public static CouponIssueAvailability inactive(final boolean issueOpen, final CouponStockSnapshot stock) {
        return from(CouponIssueReason.POLICY_NOT_ACTIVE, false, issueOpen, stock);
    }

    public static CouponIssueAvailability closed(final CouponStockSnapshot stock) {
        return from(CouponIssueReason.ISSUE_NOT_ALLOWED, true, false, stock);
    }

    public static CouponIssueAvailability soldOut(final CouponStockSnapshot stock) {
        return from(CouponIssueReason.ISSUE_NOT_ALLOWED, true, true, stock);
    }

    public static CouponIssueAvailability alreadyIssued(final CouponStockSnapshot stock) {
        return from(CouponIssueReason.ALREADY_ISSUED, true, true, stock);
    }

    public static CouponIssueAvailability issuable(final CouponStockSnapshot stock) {
        return from(CouponIssueReason.ISSUABLE, true, true, stock);
    }

    private static CouponIssueAvailability from(
        final CouponIssueReason reason,
        final boolean policyActive,
        final boolean issueOpen,
        final CouponStockSnapshot stock
    ) {
        return create(
            reason,
            policyActive,
            issueOpen,
            stock.remaining(),
            stock.alreadyIssued(),
            stock.totalQuantity(),
            stock.issuedCount()
        );
    }

    private static CouponIssueAvailability create(
        final CouponIssueReason reason,
        final boolean policyActive,
        final boolean issueOpen,
        final boolean quantityRemaining,
        final boolean alreadyIssued,
        final Long totalQuantity,
        final long issuedCount
    ) {
        return new CouponIssueAvailability(
            reason == CouponIssueReason.ISSUABLE,
            reason,
            policyActive,
            issueOpen,
            quantityRemaining,
            alreadyIssued,
            totalQuantity,
            issuedCount
        );
    }
}

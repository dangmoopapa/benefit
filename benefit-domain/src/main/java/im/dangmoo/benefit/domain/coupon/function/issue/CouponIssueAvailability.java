package im.dangmoo.benefit.domain.coupon.function.issue;

import im.dangmoo.benefit.domain.coupon.data.stock.CouponStockResult;

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

    public static CouponIssueAvailability notFound() {
        return create(CouponIssueReason.POLICY_NOT_FOUND, false, false, false, false, null, 0L);
    }

    public static CouponIssueAvailability inactive(final boolean issueOpen, final CouponStockResult stock) {
        return from(CouponIssueReason.POLICY_NOT_ACTIVE, false, issueOpen, stock);
    }

    public static CouponIssueAvailability closed(final CouponStockResult stock) {
        return from(CouponIssueReason.ISSUE_NOT_ALLOWED, true, false, stock);
    }

    public static CouponIssueAvailability soldOut(final CouponStockResult stock) {
        return from(CouponIssueReason.ISSUE_NOT_ALLOWED, true, true, stock);
    }

    public static CouponIssueAvailability alreadyIssued(final CouponStockResult stock) {
        return from(CouponIssueReason.ALREADY_ISSUED, true, true, stock);
    }

    public static CouponIssueAvailability issuable(final CouponStockResult stock) {
        return from(CouponIssueReason.ISSUABLE, true, true, stock);
    }

    private static CouponIssueAvailability from(
        final CouponIssueReason reason,
        final boolean policyActive,
        final boolean issueOpen,
        final CouponStockResult stock
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

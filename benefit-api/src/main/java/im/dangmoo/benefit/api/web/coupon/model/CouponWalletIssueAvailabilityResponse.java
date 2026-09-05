package im.dangmoo.benefit.api.web.coupon.model;

public record CouponWalletIssueAvailabilityResponse(
    boolean issuable,
    String reason,
    boolean policyActive,
    boolean issueOpen,
    boolean quantityRemaining,
    boolean alreadyIssued,
    Long totalQuantity,
    long issuedCount
) {
}

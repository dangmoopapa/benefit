package im.dangmoo.benefit.api.web.coupon.model;

public record CouponWalletIssueRequest(
    String policyId,
    boolean segmentMatched
) {
}

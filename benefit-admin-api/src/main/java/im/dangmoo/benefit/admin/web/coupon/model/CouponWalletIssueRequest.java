package im.dangmoo.benefit.admin.web.coupon.model;

public record CouponWalletIssueRequest(
    String userId,
    String policyId,
    boolean enforceIssueCondition
) {
}

package im.dangmoo.benefit.admin.model.coupon.wallet;

public record CouponWalletIssueRequest(
    String policyKey,
    String userId
) {
}

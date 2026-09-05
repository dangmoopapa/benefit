package im.dangmoo.benefit.domain.coupon.function.issue;

import im.dangmoo.benefit.domain.coupon.data.wallet.CouponWallet;

public record CouponIssueResult(
    CouponIssueReason reason,
    CouponWallet wallet
) {

    public static CouponIssueResult issued(final CouponWallet wallet) {
        return new CouponIssueResult(CouponIssueReason.ISSUED, wallet);
    }

    public static CouponIssueResult notFound() {
        return fail(CouponIssueReason.POLICY_NOT_FOUND);
    }

    public static CouponIssueResult inactive() {
        return fail(CouponIssueReason.POLICY_NOT_ACTIVE);
    }

    public static CouponIssueResult closed() {
        return fail(CouponIssueReason.ISSUE_NOT_ALLOWED);
    }

    public static CouponIssueResult soldOut() {
        return fail(CouponIssueReason.ISSUE_NOT_ALLOWED);
    }

    public static CouponIssueResult alreadyIssued() {
        return fail(CouponIssueReason.ALREADY_ISSUED);
    }

    private static CouponIssueResult fail(final CouponIssueReason reason) {
        return new CouponIssueResult(reason, null);
    }
}

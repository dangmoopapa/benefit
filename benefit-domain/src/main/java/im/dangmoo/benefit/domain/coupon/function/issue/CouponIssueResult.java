package im.dangmoo.benefit.domain.coupon.function.issue;

import im.dangmoo.benefit.domain.coupon.document.wallet.CouponWallet;

public record CouponIssueResult(
    CouponIssueReason reason,
    CouponWallet wallet
) {

    public static CouponIssueResult issued(final CouponWallet wallet) {
        return new CouponIssueResult(CouponIssueReason.ISSUED, wallet);
    }

    public static CouponIssueResult of(final CouponIssueReason reason) {
        return new CouponIssueResult(reason, null);
    }
}

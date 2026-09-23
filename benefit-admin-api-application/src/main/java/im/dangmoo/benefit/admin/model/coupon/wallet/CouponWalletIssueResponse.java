package im.dangmoo.benefit.admin.model.coupon.wallet;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletDocument;

public record CouponWalletIssueResponse(String id) {

    public static CouponWalletIssueResponse of(final CouponWalletDocument wallet) {
        return new CouponWalletIssueResponse(wallet.getId());
    }
}

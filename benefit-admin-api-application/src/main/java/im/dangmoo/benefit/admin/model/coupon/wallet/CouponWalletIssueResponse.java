package im.dangmoo.benefit.admin.model.coupon.wallet;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;

public record CouponWalletIssueResponse(String id) {

    public static CouponWalletIssueResponse of(final CouponWallet wallet) {
        return new CouponWalletIssueResponse(wallet.getId());
    }
}

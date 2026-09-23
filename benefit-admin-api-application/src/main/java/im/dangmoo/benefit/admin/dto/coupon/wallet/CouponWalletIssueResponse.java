package im.dangmoo.benefit.admin.dto.coupon.wallet;

import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;

public record CouponWalletIssueResponse(String id) {

    public static CouponWalletIssueResponse of(final CouponWalletDocument wallet) {
        return new CouponWalletIssueResponse(wallet.getId());
    }
}

package im.dangmoo.benefit.admin.model.coupon.wallet;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletStatus;

public record CouponWalletSearchRequest(
    String userId,
    String orderId,
    String policyId,
    String createdBy,
    String updatedBy,
    CouponWalletStatus status
) {
}

package im.dangmoo.benefit.admin.dto.coupon.wallet;

import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletStatus;

public record CouponWalletSearchRequest(
    String userId,
    String orderId,
    String policyId,
    String createdBy,
    String updatedBy,
    CouponWalletStatus status
) {
}

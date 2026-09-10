package im.dangmoo.benefit.domain.function.coupon.redeem;

import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletStatus;

import java.time.Instant;

public sealed interface CouponRecover {

    record Success(
        String walletId,
        String userId,
        String orderId,
        String policyId,
        String policyCode,
        CouponWalletStatus status,
        Instant recoveredAt,
        Instant expiresAt,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
    ) implements CouponRecover {
        static Success of(final CouponWallet wallet) {
            return new Success(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getOrderId(),
                wallet.getPolicyId(),
                wallet.getPolicyCode(),
                wallet.getStatus(),
                wallet.getRecoveredAt(),
                wallet.getExpiresAt(),
                wallet.getCreatedBy(),
                wallet.getCreatedAt(),
                wallet.getUpdatedBy(),
                wallet.getUpdatedAt()
            );
        }
    }

    record WalletNotFound() implements CouponRecover {
    }

    record InvalidState() implements CouponRecover {
    }
}

package im.dangmoo.benefit.domain.component.coupon.issue;

import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletStatus;

import java.time.Instant;

public sealed interface CouponIssue {

    record Success(
        String walletId,
        String userId,
        String policyId,
        String policyCode,
        String idempotencyKey,
        CouponWalletStatus status,
        Instant issuedAt,
        Instant expiresAt,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
    ) implements CouponIssue {
        static Success of(final CouponWallet wallet) {
            return new Success(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getPolicyId(),
                wallet.getPolicyCode(),
                wallet.getIdempotencyKey(),
                wallet.getStatus(),
                wallet.getIssuedAt(),
                wallet.getExpiresAt(),
                wallet.getCreatedBy(),
                wallet.getCreatedAt(),
                wallet.getUpdatedBy(),
                wallet.getUpdatedAt()
            );
        }
    }

    record PolicyNotFound() implements CouponIssue {
    }

    record PolicyNotActive() implements CouponIssue {
    }

    record NotAllowed() implements CouponIssue {
    }

    record AlreadyIssued() implements CouponIssue {
    }
}

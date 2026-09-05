package im.dangmoo.benefit.domain.function.coupon.redeem;

import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Component
public class CouponRedeemer {

    private final CouponWalletRepository couponWalletRepository;

    public CouponRedeemer(final CouponWalletRepository couponWalletRepository) {
        this.couponWalletRepository = couponWalletRepository;
    }

    public CouponUseResult use(
        final String walletId,
        final String orderId,
        final BigDecimal usedAmount,
        final String actorId,
        final boolean enforceUsable,
        final String ownerUserId
    ) {
        final Optional<CouponWallet> found = couponWalletRepository.findById(walletId)
            .filter(wallet -> wallet.belongsTo(ownerUserId));
        if (found.isEmpty()) {
            return CouponUseResult.of(CouponUseReason.WALLET_NOT_FOUND);
        }

        final CouponWallet wallet = found.get();
        if (enforceUsable) {
            if (!wallet.isUsableAt(Instant.now())) {
                return CouponUseResult.of(CouponUseReason.INVALID_STATE);
            }
        } else if (wallet.isNotAvailable()) {
            return CouponUseResult.of(CouponUseReason.INVALID_STATE);
        }

        wallet.use(orderId, usedAmount, actorId);
        return CouponUseResult.used(couponWalletRepository.save(wallet));
    }

    public CouponRecoverResult recover(final String walletId, final String actorId, final String ownerUserId) {
        final Optional<CouponWallet> found = couponWalletRepository.findById(walletId)
            .filter(wallet -> wallet.belongsTo(ownerUserId));
        if (found.isEmpty()) {
            return CouponRecoverResult.of(CouponRecoverReason.WALLET_NOT_FOUND);
        }

        final CouponWallet wallet = found.get();
        if (!wallet.isUsed()) {
            return CouponRecoverResult.of(CouponRecoverReason.INVALID_STATE);
        }

        wallet.recover(actorId);
        return CouponRecoverResult.recovered(couponWalletRepository.save(wallet));
    }
}

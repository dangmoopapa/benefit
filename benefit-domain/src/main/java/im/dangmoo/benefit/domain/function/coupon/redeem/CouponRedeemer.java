package im.dangmoo.benefit.domain.function.coupon.redeem;

import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyRepository;
import im.dangmoo.benefit.domain.data.coupon.stock.CouponUsageRepository;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Component
public class CouponRedeemer {

    private final CouponWalletRepository couponWalletRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final CouponUsageRepository couponUsageRepository;

    public CouponRedeemer(
        final CouponWalletRepository couponWalletRepository,
        final CouponPolicyRepository couponPolicyRepository,
        final CouponUsageRepository couponUsageRepository
    ) {
        this.couponWalletRepository = couponWalletRepository;
        this.couponPolicyRepository = couponPolicyRepository;
        this.couponUsageRepository = couponUsageRepository;
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

        final Long totalLimit = couponPolicyRepository.findById(wallet.getPolicyId())
            .map(CouponPolicy::totalUsageLimit)
            .orElse(null);
        if (!couponUsageRepository.tryConsume(wallet.getPolicyId(), totalLimit)) {
            return CouponUseResult.of(CouponUseReason.USAGE_LIMIT_EXCEEDED);
        }

        final Optional<CouponWallet> used = couponWalletRepository.markUsed(
            walletId,
            orderId,
            usedAmount,
            actorId
        );
        if (used.isEmpty()) {
            couponUsageRepository.release(wallet.getPolicyId());
            return CouponUseResult.of(CouponUseReason.INVALID_STATE);
        }
        return CouponUseResult.used(used.get());
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

        final Optional<CouponWallet> recovered = couponWalletRepository.markRecovered(walletId, actorId);
        if (recovered.isEmpty()) {
            return CouponRecoverResult.of(CouponRecoverReason.INVALID_STATE);
        }

        couponUsageRepository.release(wallet.getPolicyId());
        return CouponRecoverResult.recovered(recovered.get());
    }
}

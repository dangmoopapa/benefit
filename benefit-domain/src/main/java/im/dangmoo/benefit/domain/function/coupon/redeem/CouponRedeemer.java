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

    public CouponUse use(
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
            return new CouponUse.WalletNotFound();
        }

        final CouponWallet wallet = found.get();
        if (enforceUsable) {
            if (!wallet.isUsableAt(Instant.now())) {
                return new CouponUse.InvalidState();
            }
        } else if (wallet.isNotAvailable()) {
            return new CouponUse.InvalidState();
        }

        final Long totalLimit = couponPolicyRepository.findById(wallet.getPolicyId())
            .map(CouponPolicy::totalUsageLimit)
            .orElse(null);
        if (!couponUsageRepository.tryConsume(wallet.getPolicyId(), totalLimit)) {
            return new CouponUse.LimitExceeded();
        }

        final Optional<CouponWallet> used = couponWalletRepository.markUsed(
            walletId,
            orderId,
            usedAmount,
            actorId
        );
        if (used.isEmpty()) {
            couponUsageRepository.release(wallet.getPolicyId());
            return new CouponUse.InvalidState();
        }
        return CouponUse.Success.of(used.get());
    }

    public CouponRecover recover(final String walletId, final String actorId, final String ownerUserId) {
        final Optional<CouponWallet> found = couponWalletRepository.findById(walletId)
            .filter(wallet -> wallet.belongsTo(ownerUserId));
        if (found.isEmpty()) {
            return new CouponRecover.WalletNotFound();
        }

        final CouponWallet wallet = found.get();
        if (!wallet.isUsed()) {
            return new CouponRecover.InvalidState();
        }

        final Optional<CouponWallet> recovered = couponWalletRepository.markRecovered(walletId, actorId);
        if (recovered.isEmpty()) {
            return new CouponRecover.InvalidState();
        }

        couponUsageRepository.release(wallet.getPolicyId());
        return CouponRecover.Success.of(recovered.get());
    }
}

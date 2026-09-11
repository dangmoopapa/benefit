package im.dangmoo.benefit.domain.component.coupon.redeem;

import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyRepository;
import im.dangmoo.benefit.domain.data.coupon.stock.CouponUsageRepository;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletRepository;
import im.dangmoo.benefit.domain.util.TimeUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
            if (!wallet.isUsableAt(TimeUtils.now())) {
                return new CouponUse.InvalidState();
            }
        } else if (wallet.isNotAvailable()) {
            return new CouponUse.InvalidState();
        }

        final Long totalLimit = couponPolicyRepository.findById(wallet.getPolicyId())
            .map(CouponPolicy::totalUsageLimit)
            .orElse(null);
        if (!consumeUsage(wallet.getPolicyId(), totalLimit)) {
            return new CouponUse.LimitExceeded();
        }

        try {
            return CouponUse.Success.of(couponWalletRepository.save(wallet.use(orderId, usedAmount, actorId)));
        } catch (final IllegalStateException | OptimisticLockingFailureException ex) {
            if (totalLimit != null) {
                couponUsageRepository.release(wallet.getPolicyId());
            }
            return new CouponUse.InvalidState();
        }
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

        try {
            final CouponWallet recovered = couponWalletRepository.save(wallet.recover(actorId));
            couponPolicyRepository.findById(wallet.getPolicyId())
                .map(CouponPolicy::totalUsageLimit)
                .ifPresent(_ -> couponUsageRepository.release(wallet.getPolicyId()));
            return CouponRecover.Success.of(recovered);
        } catch (final IllegalStateException | OptimisticLockingFailureException ex) {
            return new CouponRecover.InvalidState();
        }
    }

    private boolean consumeUsage(final String policyId, final Long totalLimit) {
        if (totalLimit == null) {
            return true;
        }
        return couponUsageRepository.consume(policyId, totalLimit);
    }
}

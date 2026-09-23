package im.dangmoo.benefit.api.usecase.coupon;

import im.dangmoo.benefit.api.dto.coupon.CouponBoxResponse;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyCacheRepository;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponWalletMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponBoxUseCase {

    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponPolicyCacheRepository couponPolicyCacheRepository;

    public CouponBoxUseCase(
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponPolicyCacheRepository couponPolicyCacheRepository
    ) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
    }

    public CouponBoxResponse box(final String userId) {
        final List<CouponWalletDocument> wallets = couponWalletMongoRepository.findByUserId(userId);
        final Instant now = Instant.now();
        final List<CouponBoxResponse.Item> available = new ArrayList<>();
        final List<CouponBoxResponse.Item> unavailable = new ArrayList<>();

        for (final CouponWalletDocument wallet : wallets) {
            final CouponPolicyCache policy = couponPolicyCacheRepository.findByKey(wallet.getPolicyKey());
            final String policyName = policy == null ? null : policy.name();
            final CouponBoxResponse.Item item = CouponBoxResponse.Item.of(wallet, policyName);
            if (wallet.isAvailable(now)) {
                available.add(item);
            } else {
                unavailable.add(item);
            }
        }
        return new CouponBoxResponse(available, unavailable);
    }
}

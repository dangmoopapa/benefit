package im.dangmoo.benefit.api.web.coupon.service;

import im.dangmoo.benefit.api.web.coupon.model.CouponBoxResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.domain.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.coupon.wallet.CouponWalletRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponBoxService {

    private final CouponWalletRepository couponWalletRepository;

    public CouponBoxService(final CouponWalletRepository couponWalletRepository) {
        this.couponWalletRepository = couponWalletRepository;
    }

    public CouponBoxResponse get(final String userId) {
        final Instant now = Instant.now();
        final List<CouponWalletResponse> available = new ArrayList<>();
        final List<CouponWalletResponse> unavailable = new ArrayList<>();

        for (final CouponWallet wallet : couponWalletRepository.findAllByUserId(userId)) {
            final CouponWalletResponse response = CouponWalletResponse.of(wallet);
            if (wallet.isUsableAt(now)) {
                available.add(response);
            } else {
                unavailable.add(response);
            }
        }

        return new CouponBoxResponse(available, unavailable);
    }
}

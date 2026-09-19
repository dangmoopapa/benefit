package im.dangmoo.benefit.admin.usecase.coupon.wallet;

import im.dangmoo.benefit.admin.model.coupon.wallet.CouponWalletSearchRequest;
import im.dangmoo.benefit.admin.model.coupon.wallet.CouponWalletSearchResponse;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponWalletSearchUseCase {

    private final CouponWalletMongoRepository couponWalletMongoRepository;

    public CouponWalletSearchUseCase(final CouponWalletMongoRepository couponWalletMongoRepository) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
    }

    public CouponWalletSearchResponse execute(final CouponWalletSearchRequest request) {
        final List<CouponWallet> wallets = couponWalletMongoRepository.search(
            request.userId(),
            request.orderId(),
            request.policyId(),
            request.createdBy(),
            request.updatedBy(),
            request.status()
        );
        return CouponWalletSearchResponse.of(wallets);
    }
}

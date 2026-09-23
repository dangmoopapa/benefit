package im.dangmoo.benefit.admin.usecase.coupon;

import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletSearchRequest;
import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletSearchResponse;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponWalletMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponWalletSearchUseCase {

    private final CouponWalletMongoRepository couponWalletMongoRepository;

    public CouponWalletSearchUseCase(final CouponWalletMongoRepository couponWalletMongoRepository) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
    }

    public CouponWalletSearchResponse search(final CouponWalletSearchRequest request) {
        final List<CouponWalletDocument> wallets = couponWalletMongoRepository.search(
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

package im.dangmoo.benefit.api.web.coupon.service;

import im.dangmoo.benefit.api.support.ApiException;
import im.dangmoo.benefit.api.support.ApiMessage;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponRecover;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponRedeemer;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponUse;
import org.springframework.stereotype.Service;

@Service
public class CouponUseService {

    private final CouponRedeemer couponRedeemer;

    public CouponUseService(final CouponRedeemer couponRedeemer) {
        this.couponRedeemer = couponRedeemer;
    }

    public CouponWalletResponse use(final String userId, final String walletId, final CouponWalletUseRequest request) {
        return switch (couponRedeemer.use(
            walletId,
            request.orderId(),
            request.usedAmount(),
            userId,
            true,
            userId
        )) {
            case CouponUse.Success success -> CouponWalletResponse.of(success);
            case CouponUse.WalletNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case CouponUse.InvalidState _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case CouponUse.LimitExceeded _ -> throw new ApiException(ApiMessage.USAGE_LIMIT_EXCEEDED);
        };
    }

    public CouponWalletResponse cancel(final String userId, final String walletId) {
        return switch (couponRedeemer.recover(walletId, userId, userId)) {
            case CouponRecover.Success success -> CouponWalletResponse.of(success);
            case CouponRecover.WalletNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case CouponRecover.InvalidState _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
        };
    }
}

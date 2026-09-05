package im.dangmoo.benefit.api.web.coupon.service;

import im.dangmoo.benefit.api.support.ApiException;
import im.dangmoo.benefit.api.support.ApiMessage;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletBulkIssueRequest;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.domain.coupon.function.issue.CouponIssueResult;
import im.dangmoo.benefit.domain.coupon.function.issue.CouponIssuer;
import im.dangmoo.benefit.domain.coupon.function.redeem.CouponRecoverResult;
import im.dangmoo.benefit.domain.coupon.function.redeem.CouponRedeemer;
import im.dangmoo.benefit.domain.coupon.function.redeem.CouponUseResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponWalletService {

    private final CouponIssuer couponIssuer;
    private final CouponRedeemer couponRedeemer;

    public CouponWalletService(final CouponIssuer couponIssuer, final CouponRedeemer couponRedeemer) {
        this.couponIssuer = couponIssuer;
        this.couponRedeemer = couponRedeemer;
    }

    public CouponWalletResponse issue(final String userId, final CouponWalletIssueRequest request) {
        final CouponIssueResult result = couponIssuer.issue(
            userId,
            request.policyId(),
            true,
            request.segmentMatched(),
            userId
        );
        return switch (result.reason()) {
            case ISSUED -> CouponWalletResponse.of(result.wallet());
            case POLICY_NOT_FOUND -> throw new ApiException(ApiMessage.NOT_FOUND);
            case POLICY_NOT_ACTIVE -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case ISSUE_NOT_ALLOWED -> throw new ApiException(ApiMessage.ISSUE_NOT_ALLOWED);
            case ALREADY_ISSUED -> throw new ApiException(ApiMessage.ALREADY_ISSUED);
        };
    }

    public List<CouponWalletResponse> issueBulk(final String userId, final CouponWalletBulkIssueRequest request) {
        final List<CouponWalletResponse> issued = new ArrayList<>();
        for (final CouponWalletIssueRequest item : request.items()) {
            issued.add(issue(userId, item));
        }
        return issued;
    }

    public CouponWalletResponse use(final String userId, final String walletId, final CouponWalletUseRequest request) {
        final CouponUseResult result = couponRedeemer.use(
            walletId,
            request.orderId(),
            request.usedAmount(),
            userId,
            true,
            userId
        );
        return switch (result.reason()) {
            case USED -> CouponWalletResponse.of(result.wallet());
            case WALLET_NOT_FOUND -> throw new ApiException(ApiMessage.NOT_FOUND);
            case INVALID_STATE -> throw new ApiException(ApiMessage.INVALID_STATUS);
        };
    }

    public CouponWalletResponse cancel(final String userId, final String walletId) {
        final CouponRecoverResult result = couponRedeemer.recover(walletId, userId, userId);
        return switch (result.reason()) {
            case RECOVERED -> CouponWalletResponse.of(result.wallet());
            case WALLET_NOT_FOUND -> throw new ApiException(ApiMessage.NOT_FOUND);
            case INVALID_STATE -> throw new ApiException(ApiMessage.INVALID_STATUS);
        };
    }
}

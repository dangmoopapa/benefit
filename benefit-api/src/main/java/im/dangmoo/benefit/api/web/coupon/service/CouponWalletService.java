package im.dangmoo.benefit.api.web.coupon.service;

import im.dangmoo.benefit.api.support.ApiException;
import im.dangmoo.benefit.api.support.ApiMessage;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletIssueAvailabilityResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.domain.function.coupon.issue.CouponIssueAvailability;
import im.dangmoo.benefit.domain.function.coupon.issue.CouponIssueResult;
import im.dangmoo.benefit.domain.function.coupon.issue.CouponIssuer;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponRecoverResult;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponRedeemer;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponUseResult;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CouponWalletService {

    private final CouponIssuer couponIssuer;
    private final CouponRedeemer couponRedeemer;

    public CouponWalletService(final CouponIssuer couponIssuer, final CouponRedeemer couponRedeemer) {
        this.couponIssuer = couponIssuer;
        this.couponRedeemer = couponRedeemer;
    }

    public CouponWalletIssueAvailabilityResponse checkIssue(
        final String userId,
        final Collection<String> userSegmentIds,
        final CouponWalletIssueRequest request
    ) {
        final CouponIssueAvailability availability = couponIssuer.check(
            userId,
            request.policyId(),
            true,
            userSegmentIds
        );
        return new CouponWalletIssueAvailabilityResponse(
            availability.issuable(),
            availability.reason().name(),
            availability.policyActive(),
            availability.issueOpen(),
            availability.quantityRemaining(),
            availability.alreadyIssued(),
            availability.totalQuantity(),
            availability.issuedCount()
        );
    }

    public CouponWalletResponse issue(
        final String userId,
        final Collection<String> userSegmentIds,
        final CouponWalletIssueRequest request
    ) {
        final CouponIssueResult result = couponIssuer.issue(
            userId,
            request.policyId(),
            true,
            userSegmentIds,
            userId
        );
        return switch (result.reason()) {
            case ISSUED -> CouponWalletResponse.of(result.wallet());
            case ISSUABLE -> throw new ApiException(ApiMessage.INTERNAL_ERROR);
            case POLICY_NOT_FOUND -> throw new ApiException(ApiMessage.NOT_FOUND);
            case POLICY_NOT_ACTIVE -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case ISSUE_NOT_ALLOWED -> throw new ApiException(ApiMessage.ISSUE_NOT_ALLOWED);
            case ALREADY_ISSUED -> throw new ApiException(ApiMessage.ALREADY_ISSUED);
        };
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
            case USAGE_LIMIT_EXCEEDED -> throw new ApiException(ApiMessage.USAGE_LIMIT_EXCEEDED);
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

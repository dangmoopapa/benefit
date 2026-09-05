package im.dangmoo.benefit.admin.web.coupon.service;

import im.dangmoo.benefit.admin.support.ApiException;
import im.dangmoo.benefit.admin.support.ApiMessage;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.domain.function.coupon.issue.CouponIssueResult;
import im.dangmoo.benefit.domain.function.coupon.issue.CouponIssuer;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponRecoverResult;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponRedeemer;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponUseResult;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponWalletAdminService {

    private final CouponWalletRepository couponWalletRepository;
    private final CouponIssuer couponIssuer;
    private final CouponRedeemer couponRedeemer;

    public CouponWalletAdminService(
        final CouponWalletRepository couponWalletRepository,
        final CouponIssuer couponIssuer,
        final CouponRedeemer couponRedeemer
    ) {
        this.couponWalletRepository = couponWalletRepository;
        this.couponIssuer = couponIssuer;
        this.couponRedeemer = couponRedeemer;
    }

    public List<CouponWalletResponse> listByPolicyId(final String policyId) {
        return couponWalletRepository.findAllByPolicyId(policyId).stream()
            .map(CouponWalletResponse::of)
            .toList();
    }

    public List<CouponWalletResponse> listByUserId(final String userId) {
        return couponWalletRepository.findAllByUserId(userId).stream()
            .map(CouponWalletResponse::of)
            .toList();
    }

    public CouponWalletResponse issue(final String adminId, final CouponWalletIssueRequest request) {
        final CouponIssueResult result = couponIssuer.issue(
            request.userId(),
            request.policyId(),
            request.enforceIssueCondition(),
            request.segmentIds() == null ? List.of() : request.segmentIds(),
            adminId
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

    public CouponWalletResponse use(final String adminId, final String walletId, final CouponWalletUseRequest request) {
        final CouponUseResult result = couponRedeemer.use(
            walletId,
            request.orderId(),
            request.usedAmount(),
            adminId,
            false,
            null
        );
        return switch (result.reason()) {
            case USED -> CouponWalletResponse.of(result.wallet());
            case WALLET_NOT_FOUND -> throw new ApiException(ApiMessage.NOT_FOUND);
            case INVALID_STATE -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case USAGE_LIMIT_EXCEEDED -> throw new ApiException(ApiMessage.USAGE_LIMIT_EXCEEDED);
        };
    }

    public CouponWalletResponse recover(final String adminId, final String walletId) {
        final CouponRecoverResult result = couponRedeemer.recover(walletId, adminId, null);
        return switch (result.reason()) {
            case RECOVERED -> CouponWalletResponse.of(result.wallet());
            case WALLET_NOT_FOUND -> throw new ApiException(ApiMessage.NOT_FOUND);
            case INVALID_STATE -> throw new ApiException(ApiMessage.INVALID_STATUS);
        };
    }
}

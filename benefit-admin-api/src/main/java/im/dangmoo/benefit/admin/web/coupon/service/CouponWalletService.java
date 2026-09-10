package im.dangmoo.benefit.admin.web.coupon.service;

import im.dangmoo.benefit.admin.support.ApiException;
import im.dangmoo.benefit.admin.support.ApiMessage;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletRepository;
import im.dangmoo.benefit.domain.function.coupon.issue.CouponIssue;
import im.dangmoo.benefit.domain.function.coupon.issue.CouponIssuer;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponRecover;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponRedeemer;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponUse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponWalletService {

    private final CouponWalletRepository couponWalletRepository;
    private final CouponIssuer couponIssuer;
    private final CouponRedeemer couponRedeemer;

    public CouponWalletService(
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
        return switch (couponIssuer.issue(
            request.userId(),
            request.policyId(),
            request.enforceIssueCondition(),
            request.segmentIds() == null ? List.of() : request.segmentIds(),
            adminId
        )) {
            case CouponIssue.Success success -> CouponWalletResponse.of(success);
            case CouponIssue.PolicyNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case CouponIssue.PolicyNotActive _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case CouponIssue.NotAllowed _ -> throw new ApiException(ApiMessage.ISSUE_NOT_ALLOWED);
            case CouponIssue.AlreadyIssued _ -> throw new ApiException(ApiMessage.ALREADY_ISSUED);
        };
    }

    public CouponWalletResponse use(final String adminId, final String walletId, final CouponWalletUseRequest request) {
        return switch (couponRedeemer.use(
            walletId,
            request.orderId(),
            request.usedAmount(),
            adminId,
            false,
            null
        )) {
            case CouponUse.Success success -> CouponWalletResponse.of(success);
            case CouponUse.WalletNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case CouponUse.InvalidState _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case CouponUse.LimitExceeded _ -> throw new ApiException(ApiMessage.USAGE_LIMIT_EXCEEDED);
        };
    }

    public CouponWalletResponse recover(final String adminId, final String walletId) {
        return switch (couponRedeemer.recover(walletId, adminId, null)) {
            case CouponRecover.Success success -> CouponWalletResponse.of(success);
            case CouponRecover.WalletNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case CouponRecover.InvalidState _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
        };
    }
}

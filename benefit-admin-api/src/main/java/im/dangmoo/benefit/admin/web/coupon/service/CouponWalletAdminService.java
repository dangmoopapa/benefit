package im.dangmoo.benefit.admin.web.coupon.service;

import im.dangmoo.benefit.admin.support.ApiException;
import im.dangmoo.benefit.admin.support.ApiMessage;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.domain.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.domain.coupon.policy.CouponPolicyRepository;
import im.dangmoo.benefit.domain.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.coupon.wallet.CouponWalletRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CouponWalletAdminService {

    private final CouponWalletRepository couponWalletRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    public CouponWalletAdminService(
        final CouponWalletRepository couponWalletRepository,
        final CouponPolicyRepository couponPolicyRepository
    ) {
        this.couponWalletRepository = couponWalletRepository;
        this.couponPolicyRepository = couponPolicyRepository;
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

    public CouponWalletResponse issue(final CouponWalletIssueRequest request) {
        final CouponPolicy policy = couponPolicyRepository.findById(request.policyId())
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        if (!policy.isActive()) {
            throw new ApiException(ApiMessage.INVALID_STATUS);
        }

        final Instant now = Instant.now();
        if (request.enforceIssueCondition()) {
            if (!policy.isIssuableAt(now, request.segmentMatched())) {
                throw new ApiException(ApiMessage.ISSUE_NOT_ALLOWED);
            }
            final long issuedCount = couponWalletRepository.countByPolicyId(policy.getId());
            if (!policy.hasIssueQuantityRemaining(issuedCount)) {
                throw new ApiException(ApiMessage.ISSUE_NOT_ALLOWED);
            }
        }

        if (couponWalletRepository.existsByUserIdAndPolicyId(request.userId(), policy.getId())) {
            throw new ApiException(ApiMessage.ALREADY_ISSUED);
        }

        final CouponWallet wallet = CouponWallet.create(
            request.userId(),
            policy.getId(),
            policy.getCode(),
            policy.resolveExpiresAt(now)
        );
        return CouponWalletResponse.of(couponWalletRepository.save(wallet));
    }

    public CouponWalletResponse use(final String walletId, final CouponWalletUseRequest request) {
        final CouponWallet wallet = couponWalletRepository.findById(walletId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        if (!wallet.isAvailable()) {
            throw new ApiException(ApiMessage.INVALID_STATUS);
        }
        wallet.use(request.orderId(), request.usedAmount());
        return CouponWalletResponse.of(couponWalletRepository.save(wallet));
    }

    public CouponWalletResponse recover(final String walletId) {
        final CouponWallet wallet = couponWalletRepository.findById(walletId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        if (!wallet.isUsed()) {
            throw new ApiException(ApiMessage.INVALID_STATUS);
        }
        wallet.recover();
        return CouponWalletResponse.of(couponWalletRepository.save(wallet));
    }
}

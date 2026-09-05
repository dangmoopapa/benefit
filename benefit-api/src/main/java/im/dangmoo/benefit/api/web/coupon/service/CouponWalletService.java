package im.dangmoo.benefit.api.web.coupon.service;

import im.dangmoo.benefit.api.support.ApiException;
import im.dangmoo.benefit.api.support.ApiMessage;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletBulkIssueRequest;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.domain.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.domain.coupon.policy.CouponPolicyRepository;
import im.dangmoo.benefit.domain.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.coupon.wallet.CouponWalletRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponWalletService {

    private final CouponWalletRepository couponWalletRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    public CouponWalletService(
        final CouponWalletRepository couponWalletRepository,
        final CouponPolicyRepository couponPolicyRepository
    ) {
        this.couponWalletRepository = couponWalletRepository;
        this.couponPolicyRepository = couponPolicyRepository;
    }

    public CouponWalletResponse issue(final String userId, final CouponWalletIssueRequest request) {
        return CouponWalletResponse.of(issueOne(userId, request.policyId(), request.segmentMatched()));
    }

    public List<CouponWalletResponse> issueBulk(final String userId, final CouponWalletBulkIssueRequest request) {
        final List<CouponWalletResponse> issued = new ArrayList<>();
        for (final CouponWalletIssueRequest item : request.items()) {
            issued.add(CouponWalletResponse.of(issueOne(userId, item.policyId(), item.segmentMatched())));
        }
        return issued;
    }

    public CouponWalletResponse use(final String userId, final String walletId, final CouponWalletUseRequest request) {
        final CouponWallet wallet = findOwnedWallet(userId, walletId);
        if (!wallet.isUsableAt(Instant.now())) {
            throw new ApiException(ApiMessage.INVALID_STATUS);
        }
        wallet.use(request.orderId(), request.usedAmount(), userId);
        return CouponWalletResponse.of(couponWalletRepository.save(wallet));
    }

    public CouponWalletResponse cancel(final String userId, final String walletId) {
        final CouponWallet wallet = findOwnedWallet(userId, walletId);
        if (!wallet.isUsed()) {
            throw new ApiException(ApiMessage.INVALID_STATUS);
        }
        wallet.recover(userId);
        return CouponWalletResponse.of(couponWalletRepository.save(wallet));
    }

    private CouponWallet issueOne(final String userId, final String policyId, final boolean segmentMatched) {
        final CouponPolicy policy = couponPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        if (!policy.isActive()) {
            throw new ApiException(ApiMessage.INVALID_STATUS);
        }

        final Instant now = Instant.now();
        if (!policy.isIssuableAt(now, segmentMatched)) {
            throw new ApiException(ApiMessage.ISSUE_NOT_ALLOWED);
        }
        final long issuedCount = couponWalletRepository.countByPolicyId(policy.getId());
        if (!policy.hasIssueQuantityRemaining(issuedCount)) {
            throw new ApiException(ApiMessage.ISSUE_NOT_ALLOWED);
        }
        if (couponWalletRepository.existsByUserIdAndPolicyId(userId, policy.getId())) {
            throw new ApiException(ApiMessage.ALREADY_ISSUED);
        }

        return couponWalletRepository.save(CouponWallet.create(
            userId,
            policy.getId(),
            policy.getCode(),
            policy.resolveExpiresAt(now),
            userId
        ));
    }

    private CouponWallet findOwnedWallet(final String userId, final String walletId) {
        final CouponWallet wallet = couponWalletRepository.findById(walletId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        if (!wallet.getUserId().equals(userId)) {
            throw new ApiException(ApiMessage.NOT_FOUND);
        }
        return wallet;
    }
}

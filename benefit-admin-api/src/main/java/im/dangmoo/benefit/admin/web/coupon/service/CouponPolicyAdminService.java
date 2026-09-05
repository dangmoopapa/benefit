package im.dangmoo.benefit.admin.web.coupon.service;

import im.dangmoo.benefit.admin.support.ApiException;
import im.dangmoo.benefit.admin.support.ApiMessage;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicyRequest;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicyResponse;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicySearchRequest;
import im.dangmoo.benefit.domain.coupon.data.policy.CouponPolicy;
import im.dangmoo.benefit.domain.coupon.data.policy.CouponPolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponPolicyAdminService {

    private final CouponPolicyRepository couponPolicyRepository;

    public CouponPolicyAdminService(final CouponPolicyRepository couponPolicyRepository) {
        this.couponPolicyRepository = couponPolicyRepository;
    }

    public List<CouponPolicyResponse> list(final CouponPolicySearchRequest request) {
        return couponPolicyRepository.findAll(
                request.code(),
                request.name(),
                request.platformId(),
                request.type(),
                request.status()
            ).stream()
            .map(CouponPolicyResponse::of)
            .toList();
    }

    public CouponPolicyResponse get(final String policyId) {
        final CouponPolicy policy = couponPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        return CouponPolicyResponse.of(policy);
    }

    public CouponPolicyResponse create(final String adminId, final CouponPolicyRequest request) {
        if (couponPolicyRepository.existsByCode(request.code())) {
            throw new ApiException(ApiMessage.DUPLICATE_CODE);
        }
        return CouponPolicyResponse.of(couponPolicyRepository.save(request.toDocument(adminId)));
    }

    public CouponPolicyResponse update(final String adminId, final String policyId, final CouponPolicyRequest request) {
        final CouponPolicy policy = couponPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        if (policy.isActive()) {
            policy.updateIssueCondition(request.issueCondition().toDocument(), adminId);
            return CouponPolicyResponse.of(couponPolicyRepository.save(policy));
        }

        if (!policy.getCode().equals(request.code()) && couponPolicyRepository.existsByCode(request.code())) {
            throw new ApiException(ApiMessage.DUPLICATE_CODE);
        }

        policy.update(
            request.code(),
            request.name(),
            request.description(),
            request.platformId(),
            request.type(),
            request.issueCondition().toDocument(),
            request.benefitCondition().toDocument(),
            request.applyCondition().toDocument(),
            request.usageCondition().toDocument(),
            request.lifecycleCondition().toDocument(),
            adminId
        );
        return CouponPolicyResponse.of(couponPolicyRepository.save(policy));
    }

    public CouponPolicyResponse activate(final String adminId, final String policyId) {
        final CouponPolicy policy = couponPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        policy.activate(adminId);
        return CouponPolicyResponse.of(couponPolicyRepository.save(policy));
    }

    public CouponPolicyResponse suspend(final String adminId, final String policyId) {
        final CouponPolicy policy = couponPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        policy.suspend(adminId);
        return CouponPolicyResponse.of(couponPolicyRepository.save(policy));
    }
}

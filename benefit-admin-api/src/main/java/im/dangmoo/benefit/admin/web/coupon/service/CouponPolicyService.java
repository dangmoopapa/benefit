package im.dangmoo.benefit.admin.web.coupon.service;

import im.dangmoo.benefit.admin.support.ApiException;
import im.dangmoo.benefit.admin.support.ApiMessage;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicyRequest;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicyResponse;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicySearchRequest;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyChangedEvent;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyPublisher;
import im.dangmoo.benefit.domain.data.coupon.policy.CouponPolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final CouponPolicyPublisher couponPolicyPublisher;

    public CouponPolicyService(
        final CouponPolicyRepository couponPolicyRepository,
        final CouponPolicyPublisher couponPolicyPublisher
    ) {
        this.couponPolicyRepository = couponPolicyRepository;
        this.couponPolicyPublisher = couponPolicyPublisher;
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
        final CouponPolicy saved = couponPolicyRepository.save(request.toEntity(adminId));
        publish(saved);
        return CouponPolicyResponse.of(saved);
    }

    public CouponPolicyResponse update(final String adminId, final String policyId, final CouponPolicyRequest request) {
        final CouponPolicy policy = couponPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        if (policy.isActive()) {
            final CouponPolicy saved = couponPolicyRepository.save(
                policy.updateIssueCondition(request.issueCondition().toEntity(), adminId)
            );
            publish(saved);
            return CouponPolicyResponse.of(saved);
        }

        if (!policy.getCode().equals(request.code()) && couponPolicyRepository.existsByCode(request.code())) {
            throw new ApiException(ApiMessage.DUPLICATE_CODE);
        }

        final CouponPolicy saved = couponPolicyRepository.save(policy.update(
            request.code(),
            request.name(),
            request.description(),
            request.platformId(),
            request.type(),
            request.issueCondition().toEntity(),
            request.benefitCondition().toEntity(),
            request.applyCondition().toEntity(),
            request.usageCondition().toEntity(),
            request.lifecycleCondition().toEntity(),
            adminId
        ));
        publish(saved);
        return CouponPolicyResponse.of(saved);
    }

    public CouponPolicyResponse activate(final String adminId, final String policyId) {
        final CouponPolicy policy = couponPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        final CouponPolicy saved = couponPolicyRepository.save(policy.activate(adminId));
        publish(saved);
        return CouponPolicyResponse.of(saved);
    }

    public CouponPolicyResponse suspend(final String adminId, final String policyId) {
        final CouponPolicy policy = couponPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        final CouponPolicy saved = couponPolicyRepository.save(policy.suspend(adminId));
        publish(saved);
        return CouponPolicyResponse.of(saved);
    }

    private void publish(final CouponPolicy policy) {
        if (policy.getStatus() == CouponPolicyStatus.DRAFT) {
            return;
        }
        couponPolicyPublisher.publish(CouponPolicyChangedEvent.of(policy));
    }
}

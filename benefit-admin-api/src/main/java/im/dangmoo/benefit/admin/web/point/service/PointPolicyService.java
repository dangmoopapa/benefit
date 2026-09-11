package im.dangmoo.benefit.admin.web.point.service;

import im.dangmoo.benefit.admin.support.ApiException;
import im.dangmoo.benefit.admin.support.ApiMessage;
import im.dangmoo.benefit.admin.web.point.model.PointPolicyRequest;
import im.dangmoo.benefit.admin.web.point.model.PointPolicyResponse;
import im.dangmoo.benefit.admin.web.point.model.PointPolicySearchRequest;
import im.dangmoo.benefit.domain.data.point.policy.PointPolicy;
import im.dangmoo.benefit.domain.data.point.policy.PointPolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;

    public PointPolicyService(final PointPolicyRepository pointPolicyRepository) {
        this.pointPolicyRepository = pointPolicyRepository;
    }

    public List<PointPolicyResponse> list(final PointPolicySearchRequest request) {
        return pointPolicyRepository.findAll(
                request.code(),
                request.name(),
                request.platformId(),
                request.status()
            ).stream()
            .map(PointPolicyResponse::of)
            .toList();
    }

    public PointPolicyResponse get(final String policyId) {
        final PointPolicy policy = pointPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        return PointPolicyResponse.of(policy);
    }

    public PointPolicyResponse create(final String adminId, final PointPolicyRequest request) {
        if (pointPolicyRepository.existsByCode(request.code())) {
            throw new ApiException(ApiMessage.DUPLICATE_CODE);
        }
        final PointPolicy saved = pointPolicyRepository.save(request.toEntity(adminId));
        return PointPolicyResponse.of(saved);
    }

    public PointPolicyResponse update(final String adminId, final String policyId, final PointPolicyRequest request) {
        final PointPolicy policy = pointPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        if (policy.isActive()) {
            throw new ApiException(ApiMessage.INVALID_STATUS);
        }

        if (!policy.getCode().equals(request.code()) && pointPolicyRepository.existsByCode(request.code())) {
            throw new ApiException(ApiMessage.DUPLICATE_CODE);
        }

        return PointPolicyResponse.of(pointPolicyRepository.save(policy.update(
            request.code(),
            request.name(),
            request.description(),
            request.platformId(),
            request.issueCondition() == null ? null : request.issueCondition().toEntity(),
            request.expirationCondition() == null ? null : request.expirationCondition().toEntity(),
            adminId
        )));
    }

    public PointPolicyResponse activate(final String adminId, final String policyId) {
        final PointPolicy policy = pointPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        return PointPolicyResponse.of(pointPolicyRepository.save(policy.activate(adminId)));
    }

    public PointPolicyResponse suspend(final String adminId, final String policyId) {
        final PointPolicy policy = pointPolicyRepository.findById(policyId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        return PointPolicyResponse.of(pointPolicyRepository.save(policy.suspend(adminId)));
    }
}

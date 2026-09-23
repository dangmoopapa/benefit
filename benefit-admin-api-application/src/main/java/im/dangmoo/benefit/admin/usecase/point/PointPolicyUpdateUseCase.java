package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.dto.point.policy.PointPolicyUpdateRequest;
import im.dangmoo.benefit.admin.dto.point.policy.PointPolicyUpdateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.point.PointBenefitDomain;
import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PointPolicyUpdateUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;

    public PointPolicyUpdateUseCase(final PointPolicyMongoRepository pointPolicyMongoRepository) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
    }

    public PointPolicyUpdateResponse update(
        final String adminId,
        final String id,
        final PointPolicyUpdateRequest request
    ) {
        final PointPolicyDocument policy = pointPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);

        if (!PointBenefitDomain.of(request.benefitCondition().toDocument()).isGrantAmountValid()) {
            throw ApiException.conditionNotSatisfied();
        }
        if (!policy.getKey().equals(request.key())
            && pointPolicyMongoRepository.existsByKey(request.key())) {
            throw ApiException.duplicateKey();
        }

        final PointPolicyDocument saved = pointPolicyMongoRepository.save(policy.update(
            request.name(),
            request.description(),
            request.key(),
            request.benefitCondition().toDocument(),
            request.issueCondition().toDocument(),
            request.expireCondition().toDocument(),
            request.lifecycleCondition().toDocument(),
            request.accountCondition().toDocument(),
            adminId
        ));
        return PointPolicyUpdateResponse.of(saved);
    }
}

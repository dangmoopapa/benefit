package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.model.point.policy.PointPolicyUpdateRequest;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicyUpdateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PointPolicyUpdateUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;

    public PointPolicyUpdateUseCase(final PointPolicyMongoRepository pointPolicyMongoRepository) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
    }

    public PointPolicyUpdateResponse execute(
        final String adminId,
        final String id,
        final PointPolicyUpdateRequest request
    ) {
        final PointPolicy policy = pointPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);

        if (!policy.getKey().equals(request.key())
            && pointPolicyMongoRepository.existsByKey(request.key())) {
            throw ApiException.duplicateKey();
        }

        final PointPolicy saved = pointPolicyMongoRepository.save(policy.update(
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

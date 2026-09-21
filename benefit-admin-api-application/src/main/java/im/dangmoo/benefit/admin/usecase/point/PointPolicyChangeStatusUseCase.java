package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.model.point.policy.PointPolicyChangeStatusRequest;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicyChangeStatusResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PointPolicyChangeStatusUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;

    public PointPolicyChangeStatusUseCase(final PointPolicyMongoRepository pointPolicyMongoRepository) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
    }

    public PointPolicyChangeStatusResponse execute(
        final String adminId,
        final String id,
        final PointPolicyChangeStatusRequest request
    ) {
        final PointPolicy policy = pointPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        final PointPolicy changed = policy.changeStatus(request.status(), adminId);
        final PointPolicy saved = pointPolicyMongoRepository.save(changed);
        return PointPolicyChangeStatusResponse.of(saved);
    }
}

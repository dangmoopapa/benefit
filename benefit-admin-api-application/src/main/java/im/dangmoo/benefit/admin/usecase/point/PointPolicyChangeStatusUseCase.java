package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.dto.point.policy.PointPolicyChangeStatusRequest;
import im.dangmoo.benefit.admin.dto.point.policy.PointPolicyChangeStatusResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PointPolicyChangeStatusUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;

    public PointPolicyChangeStatusUseCase(final PointPolicyMongoRepository pointPolicyMongoRepository) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
    }

    public PointPolicyChangeStatusResponse changeStatus(
        final String adminId,
        final String id,
        final PointPolicyChangeStatusRequest request
    ) {
        final PointPolicyDocument policy = pointPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        final PointPolicyDocument saved = pointPolicyMongoRepository.save(
            policy.changeStatus(request.status(), adminId)
        );
        return PointPolicyChangeStatusResponse.of(saved);
    }
}

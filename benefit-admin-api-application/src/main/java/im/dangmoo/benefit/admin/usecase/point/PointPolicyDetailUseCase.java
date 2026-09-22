package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.model.point.policy.PointPolicyDetailResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PointPolicyDetailUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;

    public PointPolicyDetailUseCase(final PointPolicyMongoRepository pointPolicyMongoRepository) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
    }

    public PointPolicyDetailResponse detail(final String id) {
        final PointPolicy policy = pointPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        return PointPolicyDetailResponse.of(policy);
    }
}

package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.dto.point.policy.PointPolicyDetailResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PointPolicyDetailUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;

    public PointPolicyDetailUseCase(final PointPolicyMongoRepository pointPolicyMongoRepository) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
    }

    public PointPolicyDetailResponse detail(final String id) {
        final PointPolicyDocument policy = pointPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        return PointPolicyDetailResponse.of(policy);
    }
}

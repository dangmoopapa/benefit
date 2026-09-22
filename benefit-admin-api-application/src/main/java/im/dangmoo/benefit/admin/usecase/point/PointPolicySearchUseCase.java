package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.model.point.policy.PointPolicySearchRequest;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicySearchResponse;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointPolicySearchUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;

    public PointPolicySearchUseCase(final PointPolicyMongoRepository pointPolicyMongoRepository) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
    }

    public PointPolicySearchResponse search(final PointPolicySearchRequest request) {
        final String key = request.key();
        final String name = request.name();
        final PointPolicyStatus status = request.status();
        final List<PointPolicy> policies = pointPolicyMongoRepository.search(key, name, status);
        return PointPolicySearchResponse.of(policies);
    }
}

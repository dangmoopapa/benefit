package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.model.point.policy.PointPolicyCreateRequest;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicyCreateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.point.PointBenefitDomain;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireType;
import org.springframework.stereotype.Service;

@Service
public class PointPolicyCreateUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;

    public PointPolicyCreateUseCase(final PointPolicyMongoRepository pointPolicyMongoRepository) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
    }

    public PointPolicyCreateResponse create(final String adminId, final PointPolicyCreateRequest request) {
        if (!PointBenefitDomain.of(request.benefitCondition().toDocument()).isValid()) {
            throw ApiException.conditionNotSatisfied();
        }
        final var expire = request.expireCondition();
        if (expire.type() == PointExpireType.FIXED_AT && expire.expiresAt() == null) {
            throw ApiException.conditionNotSatisfied();
        }
        if (expire.type() == PointExpireType.DAYS_AFTER_GRANT
            && (expire.daysAfterGrant() == null || expire.daysAfterGrant() <= 0)) {
            throw ApiException.conditionNotSatisfied();
        }

        if (pointPolicyMongoRepository.existsByKey(request.key())) {
            throw ApiException.duplicateKey();
        }
        final PointPolicy saved = pointPolicyMongoRepository.save(request.toDocument(adminId));
        return PointPolicyCreateResponse.of(saved);
    }
}

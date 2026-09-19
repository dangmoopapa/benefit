package im.dangmoo.benefit.admin.usecase.coupon.policy;

import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyUpdateRequest;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyUpdateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCacheRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class CouponPolicyUpdateUseCase {

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;
    private final CouponPolicyCacheRepository couponPolicyCacheRepository;

    public CouponPolicyUpdateUseCase(
        final CouponPolicyMongoRepository couponPolicyMongoRepository,
        final CouponPolicyCacheRepository couponPolicyCacheRepository
    ) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
    }

    public CouponPolicyUpdateResponse execute(
        final String adminId,
        final String id,
        final CouponPolicyUpdateRequest request
    ) {
        final CouponPolicy policy = couponPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);

        final String policyKey = policy.getKey();
        final String requestPolicyKey = request.key();
        final boolean keyChanged = !policyKey.equals(requestPolicyKey);
        final boolean keyExists = couponPolicyMongoRepository.existsByKey(requestPolicyKey);
        if (keyChanged && keyExists) {
            throw ApiException.duplicateKey();
        }

        final CouponPolicy updated = policy.update(
            request.name(),
            request.description(),
            requestPolicyKey,
            request.type(),
            request.benefitCondition().toDocument(),
            request.issueCondition().toDocument(),
            request.usageCondition().toDocument(),
            request.applyCondition().toDocument(),
            request.lifecycleCondition().toDocument(),
            request.accountCondition().toDocument(),
            adminId
        );
        final CouponPolicy saved = couponPolicyMongoRepository.save(updated);
        couponPolicyCacheRepository.put(saved);
        return CouponPolicyUpdateResponse.of(saved);
    }
}

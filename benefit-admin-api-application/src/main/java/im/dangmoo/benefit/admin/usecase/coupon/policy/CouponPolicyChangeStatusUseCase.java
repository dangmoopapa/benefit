package im.dangmoo.benefit.admin.usecase.coupon.policy;

import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyChangeStatusRequest;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyChangeStatusResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCacheRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedEvent;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class CouponPolicyChangeStatusUseCase {

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;
    private final CouponPolicyCacheRepository couponPolicyCacheRepository;
    private final CouponPolicyChangedPublisher couponPolicyChangedPublisher;

    public CouponPolicyChangeStatusUseCase(
        final CouponPolicyMongoRepository couponPolicyMongoRepository,
        final CouponPolicyCacheRepository couponPolicyCacheRepository,
        final CouponPolicyChangedPublisher couponPolicyChangedPublisher
    ) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
        this.couponPolicyChangedPublisher = couponPolicyChangedPublisher;
    }

    public CouponPolicyChangeStatusResponse execute(
        final String adminId,
        final String id,
        final CouponPolicyChangeStatusRequest request
    ) {
        final CouponPolicy policy = couponPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        final CouponPolicy changed = policy.changeStatus(request.status(), adminId);
        final CouponPolicy saved = couponPolicyMongoRepository.save(changed);
        couponPolicyCacheRepository.put(saved);
        couponPolicyChangedPublisher.publish(CouponPolicyChangedEvent.ofStatusChanged(saved));
        return CouponPolicyChangeStatusResponse.of(saved);
    }
}

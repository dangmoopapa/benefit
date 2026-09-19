package im.dangmoo.benefit.admin.usecase.coupon.policy;

import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyDetailResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class CouponPolicyDetailUseCase {

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;

    public CouponPolicyDetailUseCase(final CouponPolicyMongoRepository couponPolicyMongoRepository) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
    }

    public CouponPolicyDetailResponse execute(final String id) {
        final CouponPolicy policy = couponPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        return CouponPolicyDetailResponse.of(policy);
    }
}

package im.dangmoo.benefit.admin.usecase.coupon;

import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicySearchRequest;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicySearchResponse;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyType;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponPolicySearchUseCase {

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;

    public CouponPolicySearchUseCase(final CouponPolicyMongoRepository couponPolicyMongoRepository) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
    }

    public CouponPolicySearchResponse search(final CouponPolicySearchRequest request) {
        final String key = request.key();
        final String name = request.name();
        final CouponPolicyType type = request.type();
        final CouponPolicyStatus status = request.status();
        final List<CouponPolicy> policies = couponPolicyMongoRepository.search(key, name, type, status);
        return CouponPolicySearchResponse.of(policies);
    }
}

package im.dangmoo.benefit.admin.usecase.coupon.policy;

import im.dangmoo.benefit.admin.model.coupon.policy.VoucherCouponPolicySearchRequest;
import im.dangmoo.benefit.admin.model.coupon.policy.VoucherCouponPolicySearchResponse;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherCouponPolicySearchUseCase {

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;

    public VoucherCouponPolicySearchUseCase(final CouponPolicyMongoRepository couponPolicyMongoRepository) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
    }

    public VoucherCouponPolicySearchResponse execute(final VoucherCouponPolicySearchRequest request) {
        final List<CouponPolicy> policies = couponPolicyMongoRepository.findVouchers(
            request.productId(),
            request.brandId(),
            request.status()
        );
        return VoucherCouponPolicySearchResponse.of(policies);
    }
}

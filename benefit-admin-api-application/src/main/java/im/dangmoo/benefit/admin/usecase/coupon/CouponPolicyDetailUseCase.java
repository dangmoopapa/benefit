package im.dangmoo.benefit.admin.usecase.coupon;

import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyDetailResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCode;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponPolicyDetailUseCase {

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;
    private final CouponCodeMongoRepository couponCodeMongoRepository;

    public CouponPolicyDetailUseCase(
        final CouponPolicyMongoRepository couponPolicyMongoRepository,
        final CouponCodeMongoRepository couponCodeMongoRepository
    ) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
        this.couponCodeMongoRepository = couponCodeMongoRepository;
    }

    public CouponPolicyDetailResponse execute(final String id) {
        final CouponPolicy policy = couponPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        final List<CouponCode> codes = couponCodeMongoRepository.findByPolicyId(policy.getId());
        return CouponPolicyDetailResponse.of(policy, codes);
    }
}

package im.dangmoo.benefit.admin.usecase.coupon;

import im.dangmoo.benefit.admin.dto.coupon.policy.CouponPolicyDetailResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.data.entity.coupon.code.CouponCodeDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponCodeMongoRepository;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponPolicyMongoRepository;
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

    public CouponPolicyDetailResponse detail(final String id) {
        final CouponPolicyDocument policy = couponPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        final List<CouponCodeDocument> codes = couponCodeMongoRepository.findByPolicyId(policy.getId());
        return CouponPolicyDetailResponse.of(policy, codes);
    }
}

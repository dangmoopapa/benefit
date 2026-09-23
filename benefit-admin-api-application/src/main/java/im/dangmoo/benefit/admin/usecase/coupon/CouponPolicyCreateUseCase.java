package im.dangmoo.benefit.admin.usecase.coupon;

import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyCreateRequest;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyCreateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyCacheRepository;
import im.dangmoo.benefit.infrastructure.data.batch.BatchCenterJobTriggerProduction;
import im.dangmoo.benefit.infrastructure.data.batch.BatchCenterJobTriggerPublisher;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeType;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.*;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.changed.CouponPolicyChangedPublication;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.changed.CouponPolicyChangedPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

@Service
public class CouponPolicyCreateUseCase {

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;
    private final CouponPolicyCacheRepository couponPolicyCacheRepository;
    private final CouponPolicyChangedPublisher couponPolicyChangedPublisher;
    private final BatchCenterJobTriggerPublisher batchCenterJobTriggerPublisher;
    private final CouponCodeMongoRepository couponCodeMongoRepository;

    public CouponPolicyCreateUseCase(
        final CouponPolicyMongoRepository couponPolicyMongoRepository,
        final CouponPolicyCacheRepository couponPolicyCacheRepository,
        final CouponPolicyChangedPublisher couponPolicyChangedPublisher,
        final BatchCenterJobTriggerPublisher batchCenterJobTriggerPublisher,
        final CouponCodeMongoRepository couponCodeMongoRepository
    ) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
        this.couponPolicyCacheRepository = couponPolicyCacheRepository;
        this.couponPolicyChangedPublisher = couponPolicyChangedPublisher;
        this.batchCenterJobTriggerPublisher = batchCenterJobTriggerPublisher;
        this.couponCodeMongoRepository = couponCodeMongoRepository;
    }

    public CouponPolicyCreateResponse create(final String adminId, final CouponPolicyCreateRequest request) {
        final String key = request.key();
        final boolean exists = couponPolicyMongoRepository.existsByKey(key);
        if (exists) {
            throw ApiException.duplicateKey();
        }

        if (request.type() == CouponPolicyType.MARKETING) {
            final Long issueStock = request.issueCondition().stockQuantity();
            final Long usageStock = request.usageCondition().stockQuantity();
            if (!Objects.equals(issueStock, usageStock)) {
                throw ApiException.conditionNotSatisfied();
            }
            if (!StringUtils.hasText(request.code()) && (issueStock == null || issueStock <= 0)) {
                throw ApiException.conditionNotSatisfied();
            }
        }

        final CouponPolicyDocument policy = request.toDocument(adminId);
        final CouponPolicyDocument saved = couponPolicyMongoRepository.save(policy);
        couponPolicyCacheRepository.put(saved);

        final String code = request.code();
        if (request.type() == CouponPolicyType.MARKETING && StringUtils.hasText(code)) {
            if (couponCodeMongoRepository.existsByCode(code)) {
                throw ApiException.duplicateKey();
            }
            couponCodeMongoRepository.insert(
                CouponCodeDocument.create(saved.getId(), saved.getKey(), code, CouponCodeType.FIXED, adminId)
            );
        } else if (request.type() == CouponPolicyType.MARKETING) {
            batchCenterJobTriggerPublisher.publish(BatchCenterJobTriggerProduction.of(
                "COUPON_CODE_GENERATION",
                Map.of(
                    "policyKey", saved.getKey(),
                    "requestedBy", adminId
                )
            ));
        }
        couponPolicyChangedPublisher.publish(CouponPolicyChangedPublication.ofCreated(saved));
        return CouponPolicyCreateResponse.of(saved);
    }
}

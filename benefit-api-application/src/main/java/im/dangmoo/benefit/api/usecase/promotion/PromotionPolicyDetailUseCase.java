package im.dangmoo.benefit.api.usecase.promotion;

import im.dangmoo.benefit.api.model.promotion.PromotionPolicyDetailResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.promotion.PromotionPolicyDomain;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplierMongoRepository;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PromotionPolicyDetailUseCase {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;
    private final PromotionApplierMongoRepository promotionApplierMongoRepository;

    public PromotionPolicyDetailUseCase(
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository,
        final PromotionApplierMongoRepository promotionApplierMongoRepository
    ) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
        this.promotionApplierMongoRepository = promotionApplierMongoRepository;
    }

    public PromotionPolicyDetailResponse detail(final String userId, final String key) {
        final PromotionPolicy policy = promotionPolicyMongoRepository.findByKey(key)
            .orElseThrow(ApiException::notFound);

        final PromotionPolicyDomain domain = PromotionPolicyDomain.of(policy);
        final Instant now = Instant.now();
        if (!domain.isLive(now)) {
            throw ApiException.invalidStatus();
        }

        final boolean applied = domain.hasEntry()
            && promotionApplierMongoRepository.existsByPolicyIdAndUserId(policy.getId(), userId);

        return PromotionPolicyDetailResponse.of(policy, applied);
    }
}

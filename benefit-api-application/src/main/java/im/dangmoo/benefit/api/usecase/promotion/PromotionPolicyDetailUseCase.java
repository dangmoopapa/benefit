package im.dangmoo.benefit.api.usecase.promotion;

import im.dangmoo.benefit.api.dto.promotion.PromotionPolicyDetailResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.promotion.PromotionEntryDomain;
import im.dangmoo.benefit.domain.promotion.PromotionPolicyDomain;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionApplierMongoRepository;
import im.dangmoo.benefit.data.entity.promotion.policy.PromotionPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionPolicyMongoRepository;
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
        final PromotionPolicyDocument policy = promotionPolicyMongoRepository.findByKey(key)
            .orElseThrow(ApiException::notFound);

        final PromotionPolicyDomain promotion = PromotionPolicyDomain.of(policy);
        final Instant now = Instant.now();
        if (!promotion.isOpenAt(now)) {
            throw ApiException.invalidStatus();
        }

        final boolean applied = PromotionEntryDomain.findIn(policy).isPresent()
            && promotionApplierMongoRepository.existsByPolicyIdAndUserId(policy.getId(), userId);

        return PromotionPolicyDetailResponse.of(policy, applied);
    }
}

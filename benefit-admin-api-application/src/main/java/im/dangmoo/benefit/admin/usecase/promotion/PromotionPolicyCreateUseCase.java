package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.dto.promotion.policy.PromotionPolicyCreateRequest;
import im.dangmoo.benefit.admin.dto.promotion.policy.PromotionPolicyCreateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.promotion.PromotionFeatureDomain;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionPolicyCreateUseCase {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;

    public PromotionPolicyCreateUseCase(
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository
    ) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
    }

    public PromotionPolicyCreateResponse create(
        final String adminId,
        final PromotionPolicyCreateRequest request
    ) {
        if (promotionPolicyMongoRepository.existsByKey(request.key())) {
            throw ApiException.duplicateKey();
        }
        if (!PromotionFeatureDomain.of(request.features()).isConfigured()) {
            throw ApiException.invalidPromotionFeature();
        }
        final var saved = promotionPolicyMongoRepository.save(request.toDocument(adminId));
        return PromotionPolicyCreateResponse.of(saved);
    }
}

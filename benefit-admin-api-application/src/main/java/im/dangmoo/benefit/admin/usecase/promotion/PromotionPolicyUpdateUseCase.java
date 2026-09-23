package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.dto.promotion.policy.PromotionPolicyUpdateRequest;
import im.dangmoo.benefit.admin.dto.promotion.policy.PromotionPolicyUpdateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.promotion.PromotionFeatureDomain;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionPolicyUpdateUseCase {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;

    public PromotionPolicyUpdateUseCase(
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository
    ) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
    }

    public PromotionPolicyUpdateResponse update(
        final String adminId,
        final String id,
        final PromotionPolicyUpdateRequest request
    ) {
        final var policy = promotionPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        if (!PromotionFeatureDomain.of(request.features()).isConfigured()) {
            throw ApiException.invalidPromotionFeature();
        }
        final var saved = promotionPolicyMongoRepository.save(request.toUpdate(policy, adminId));
        return PromotionPolicyUpdateResponse.of(saved);
    }
}

package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.dto.promotion.policy.PromotionPolicyChangeStatusRequest;
import im.dangmoo.benefit.admin.dto.promotion.policy.PromotionPolicyChangeStatusResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionPolicyChangeStatusUseCase {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;

    public PromotionPolicyChangeStatusUseCase(
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository
    ) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
    }

    public PromotionPolicyChangeStatusResponse changeStatus(
        final String adminId,
        final String id,
        final PromotionPolicyChangeStatusRequest request
    ) {
        final var policy = promotionPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        final var saved = promotionPolicyMongoRepository.save(
            policy.changeStatus(request.status(), adminId)
        );
        return PromotionPolicyChangeStatusResponse.of(saved);
    }
}

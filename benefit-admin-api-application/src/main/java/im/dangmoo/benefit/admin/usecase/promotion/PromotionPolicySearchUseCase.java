package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicySearchRequest;
import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicySearchResponse;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionPolicySearchUseCase {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;

    public PromotionPolicySearchUseCase(
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository
    ) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
    }

    public PromotionPolicySearchResponse search(final PromotionPolicySearchRequest request) {
        return PromotionPolicySearchResponse.of(
            promotionPolicyMongoRepository.search(
                request.key(),
                request.title(),
                request.status()
            )
        );
    }
}

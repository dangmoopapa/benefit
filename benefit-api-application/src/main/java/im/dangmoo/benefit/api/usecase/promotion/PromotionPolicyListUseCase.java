package im.dangmoo.benefit.api.usecase.promotion;

import im.dangmoo.benefit.api.dto.promotion.PromotionPolicyListResponse;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionPolicyMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PromotionPolicyListUseCase {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;

    public PromotionPolicyListUseCase(final PromotionPolicyMongoRepository promotionPolicyMongoRepository) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
    }

    public PromotionPolicyListResponse list() {
        return PromotionPolicyListResponse.of(
            promotionPolicyMongoRepository.findActiveList(Instant.now())
        );
    }
}

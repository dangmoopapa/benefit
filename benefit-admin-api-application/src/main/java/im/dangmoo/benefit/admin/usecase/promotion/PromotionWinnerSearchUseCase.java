package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.model.promotion.winner.PromotionWinnerListResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.promotion.winner.PromotionWinnerMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionWinnerSearchUseCase {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;
    private final PromotionWinnerMongoRepository promotionWinnerMongoRepository;

    public PromotionWinnerSearchUseCase(
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository,
        final PromotionWinnerMongoRepository promotionWinnerMongoRepository
    ) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
        this.promotionWinnerMongoRepository = promotionWinnerMongoRepository;
    }

    public PromotionWinnerListResponse search(final String policyId) {
        promotionPolicyMongoRepository.findById(policyId)
            .orElseThrow(ApiException::notFound);
        return PromotionWinnerListResponse.of(
            promotionWinnerMongoRepository.findByPolicyId(policyId)
        );
    }
}

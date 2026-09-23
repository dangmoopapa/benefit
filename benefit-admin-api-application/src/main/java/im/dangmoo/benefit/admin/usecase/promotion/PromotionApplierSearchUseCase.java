package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.dto.promotion.applier.PromotionApplierSearchResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionApplierMongoRepository;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionApplierSearchUseCase {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;
    private final PromotionApplierMongoRepository promotionApplierMongoRepository;

    public PromotionApplierSearchUseCase(
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository,
        final PromotionApplierMongoRepository promotionApplierMongoRepository
    ) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
        this.promotionApplierMongoRepository = promotionApplierMongoRepository;
    }

    public PromotionApplierSearchResponse search(final String policyId) {
        promotionPolicyMongoRepository.findById(policyId)
            .orElseThrow(ApiException::notFound);
        return PromotionApplierSearchResponse.of(
            promotionApplierMongoRepository.findByPolicyId(policyId)
        );
    }
}

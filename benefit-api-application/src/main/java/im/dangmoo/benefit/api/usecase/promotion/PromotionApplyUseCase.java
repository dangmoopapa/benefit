package im.dangmoo.benefit.api.usecase.promotion;

import im.dangmoo.benefit.api.model.promotion.PromotionApplyResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.promotion.PromotionEntryDomain;
import im.dangmoo.benefit.domain.promotion.PromotionPolicyDomain;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplierDocument;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplierMongoRepository;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PromotionApplyUseCase {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;
    private final PromotionApplierMongoRepository promotionApplierMongoRepository;

    public PromotionApplyUseCase(
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository,
        final PromotionApplierMongoRepository promotionApplierMongoRepository
    ) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
        this.promotionApplierMongoRepository = promotionApplierMongoRepository;
    }

    public PromotionApplyResponse apply(final String userId, final String key) {
        final PromotionPolicyDocument policy = promotionPolicyMongoRepository.findByKey(key)
            .orElseThrow(ApiException::notFound);

        if (PromotionEntryDomain.findIn(policy).isEmpty()) {
            throw ApiException.invalidPromotion();
        }

        final Instant now = Instant.now();
        final boolean alreadyApplied = promotionApplierMongoRepository.existsByPolicyIdAndUserId(
            policy.getId(),
            userId
        );

        switch (PromotionPolicyDomain.of(policy).applicabilityAt(now, alreadyApplied)) {
            case ALREADY_APPLIED -> throw ApiException.alreadyAppliedPromotion();
            case NOT_OPEN -> throw ApiException.invalidPromotion();
            case APPLICABLE -> {
            }
        }

        final PromotionApplierDocument applier = promotionApplierMongoRepository.save(
            PromotionApplierDocument.apply(policy.getId(), policy.getKey(), userId)
        );
        return PromotionApplyResponse.of(applier);
    }
}

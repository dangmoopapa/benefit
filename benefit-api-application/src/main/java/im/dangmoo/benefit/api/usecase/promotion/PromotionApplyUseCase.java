package im.dangmoo.benefit.api.usecase.promotion;

import im.dangmoo.benefit.api.model.promotion.PromotionApplyResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.promotion.PromotionEntryDomain;
import im.dangmoo.benefit.domain.promotion.PromotionPolicyDomain;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplier;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplierMongoRepository;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;
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
        final PromotionPolicy policy = promotionPolicyMongoRepository.findByKey(key)
            .orElseThrow(ApiException::notFound);

        final Instant now = Instant.now();
        final boolean alreadyApplied = promotionApplierMongoRepository.existsByPolicyIdAndUserId(
            policy.getId(),
            userId
        );

        try {
            PromotionPolicyDomain.of(policy).requireApplicable(now, alreadyApplied);
        } catch (final PromotionEntryDomain.AlreadyAppliedException ex) {
            throw ApiException.alreadyAppliedPromotion();
        } catch (final PromotionPolicyDomain.NotApplicableException ex) {
            throw ApiException.invalidPromotion();
        }

        final PromotionApplier applier = promotionApplierMongoRepository.save(
            PromotionApplier.apply(policy.getId(), policy.getKey(), userId)
        );
        return PromotionApplyResponse.of(applier);
    }
}

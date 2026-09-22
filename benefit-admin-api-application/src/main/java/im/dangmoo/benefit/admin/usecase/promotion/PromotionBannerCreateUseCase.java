package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.model.promotion.banner.PromotionBannerCreateRequest;
import im.dangmoo.benefit.admin.model.promotion.banner.PromotionBannerCreateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionBannerCreateUseCase {

    private final PromotionBannerMongoRepository promotionBannerMongoRepository;

    public PromotionBannerCreateUseCase(
        final PromotionBannerMongoRepository promotionBannerMongoRepository
    ) {
        this.promotionBannerMongoRepository = promotionBannerMongoRepository;
    }

    public PromotionBannerCreateResponse create(
        final String adminId,
        final PromotionBannerCreateRequest request
    ) {
        if (promotionBannerMongoRepository.existsByKey(request.key())) {
            throw ApiException.duplicateKey();
        }
        final var saved = promotionBannerMongoRepository.save(request.toDocument(adminId));
        return PromotionBannerCreateResponse.of(saved);
    }
}

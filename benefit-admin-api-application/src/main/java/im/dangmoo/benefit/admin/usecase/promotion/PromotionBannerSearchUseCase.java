package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.model.promotion.banner.PromotionBannerSearchRequest;
import im.dangmoo.benefit.admin.model.promotion.banner.PromotionBannerSearchResponse;
import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionBannerSearchUseCase {

    private final PromotionBannerMongoRepository promotionBannerMongoRepository;

    public PromotionBannerSearchUseCase(
        final PromotionBannerMongoRepository promotionBannerMongoRepository
    ) {
        this.promotionBannerMongoRepository = promotionBannerMongoRepository;
    }

    public PromotionBannerSearchResponse search(final PromotionBannerSearchRequest request) {
        return PromotionBannerSearchResponse.of(
            promotionBannerMongoRepository.search(
                request.key(),
                request.name(),
                request.status()
            )
        );
    }
}

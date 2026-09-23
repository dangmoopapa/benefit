package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.dto.promotion.banner.PromotionBannerDetailResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.data.infrastructure.promotion.PromotionBannerMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionBannerDetailUseCase {

    private final PromotionBannerMongoRepository promotionBannerMongoRepository;

    public PromotionBannerDetailUseCase(
        final PromotionBannerMongoRepository promotionBannerMongoRepository
    ) {
        this.promotionBannerMongoRepository = promotionBannerMongoRepository;
    }

    public PromotionBannerDetailResponse detail(final String id) {
        return promotionBannerMongoRepository.findById(id)
            .map(PromotionBannerDetailResponse::of)
            .orElseThrow(ApiException::notFound);
    }
}

package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.model.promotion.banner.PromotionBannerUpdateRequest;
import im.dangmoo.benefit.admin.model.promotion.banner.PromotionBannerUpdateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionBannerUpdateUseCase {

    private final PromotionBannerMongoRepository promotionBannerMongoRepository;

    public PromotionBannerUpdateUseCase(
        final PromotionBannerMongoRepository promotionBannerMongoRepository
    ) {
        this.promotionBannerMongoRepository = promotionBannerMongoRepository;
    }

    public PromotionBannerUpdateResponse update(
        final String adminId,
        final String id,
        final PromotionBannerUpdateRequest request
    ) {
        final var banner = promotionBannerMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        final var saved = promotionBannerMongoRepository.save(request.toUpdate(banner, adminId));
        return PromotionBannerUpdateResponse.of(saved);
    }
}

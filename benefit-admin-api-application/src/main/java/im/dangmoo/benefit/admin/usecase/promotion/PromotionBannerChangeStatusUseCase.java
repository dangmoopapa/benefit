package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.model.promotion.banner.PromotionBannerChangeStatusRequest;
import im.dangmoo.benefit.admin.model.promotion.banner.PromotionBannerChangeStatusResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.promotion.banner.PromotionBannerMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PromotionBannerChangeStatusUseCase {

    private final PromotionBannerMongoRepository promotionBannerMongoRepository;

    public PromotionBannerChangeStatusUseCase(
        final PromotionBannerMongoRepository promotionBannerMongoRepository
    ) {
        this.promotionBannerMongoRepository = promotionBannerMongoRepository;
    }

    public PromotionBannerChangeStatusResponse changeStatus(
        final String adminId,
        final String id,
        final PromotionBannerChangeStatusRequest request
    ) {
        final var banner = promotionBannerMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        final var saved = promotionBannerMongoRepository.save(
            banner.changeStatus(request.status(), adminId)
        );
        return PromotionBannerChangeStatusResponse.of(saved);
    }
}

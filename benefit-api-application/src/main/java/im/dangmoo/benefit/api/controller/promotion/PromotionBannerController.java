package im.dangmoo.benefit.api.controller.promotion;

import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.model.promotion.PromotionBannerResponse;
import im.dangmoo.benefit.api.usecase.promotion.PromotionBannerUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromotionBannerController {

    private final PromotionBannerUseCase promotionBannerUseCase;

    public PromotionBannerController(final PromotionBannerUseCase promotionBannerUseCase) {
        this.promotionBannerUseCase = promotionBannerUseCase;
    }

    @GetMapping(ApiPath.PROMOTION_BANNERS)
    ApiResponse<PromotionBannerResponse> banner(@PathVariable final String key) {
        return ApiResponse.of(promotionBannerUseCase.banner(key));
    }
}

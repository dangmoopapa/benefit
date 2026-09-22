package im.dangmoo.benefit.api.controller.promotion;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.model.promotion.PromotionApplyResponse;
import im.dangmoo.benefit.api.model.promotion.PromotionPolicyDetailResponse;
import im.dangmoo.benefit.api.model.promotion.PromotionPolicyListResponse;
import im.dangmoo.benefit.api.usecase.promotion.PromotionApplyUseCase;
import im.dangmoo.benefit.api.usecase.promotion.PromotionPolicyDetailUseCase;
import im.dangmoo.benefit.api.usecase.promotion.PromotionPolicyListUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromotionController {

    private final PromotionPolicyListUseCase promotionPolicyListUseCase;
    private final PromotionPolicyDetailUseCase promotionPolicyDetailUseCase;
    private final PromotionApplyUseCase promotionApplyUseCase;

    public PromotionController(
        final PromotionPolicyListUseCase promotionPolicyListUseCase,
        final PromotionPolicyDetailUseCase promotionPolicyDetailUseCase,
        final PromotionApplyUseCase promotionApplyUseCase
    ) {
        this.promotionPolicyListUseCase = promotionPolicyListUseCase;
        this.promotionPolicyDetailUseCase = promotionPolicyDetailUseCase;
        this.promotionApplyUseCase = promotionApplyUseCase;
    }

    @GetMapping(ApiPath.PROMOTIONS)
    ApiResponse<PromotionPolicyListResponse> list() {
        return ApiResponse.of(promotionPolicyListUseCase.list());
    }

    @GetMapping(ApiPath.PROMOTION)
    ApiResponse<PromotionPolicyDetailResponse> detail(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @PathVariable final String key
    ) {
        return ApiResponse.of(promotionPolicyDetailUseCase.detail(userId, key));
    }

    @PostMapping(ApiPath.PROMOTION_APPLY)
    ApiResponse<PromotionApplyResponse> apply(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @PathVariable final String key
    ) {
        return ApiResponse.of(promotionApplyUseCase.apply(userId, key));
    }
}

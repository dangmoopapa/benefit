package im.dangmoo.benefit.admin.controller.promotion;

import im.dangmoo.benefit.admin.controller.AdminApiHeaders;
import im.dangmoo.benefit.admin.controller.AdminApiPath;
import im.dangmoo.benefit.admin.controller.AdminApiResponse;
import im.dangmoo.benefit.admin.dto.promotion.banner.PromotionBannerChangeStatusRequest;
import im.dangmoo.benefit.admin.dto.promotion.banner.PromotionBannerChangeStatusResponse;
import im.dangmoo.benefit.admin.dto.promotion.banner.PromotionBannerCreateRequest;
import im.dangmoo.benefit.admin.dto.promotion.banner.PromotionBannerCreateResponse;
import im.dangmoo.benefit.admin.dto.promotion.banner.PromotionBannerDetailResponse;
import im.dangmoo.benefit.admin.dto.promotion.banner.PromotionBannerSearchRequest;
import im.dangmoo.benefit.admin.dto.promotion.banner.PromotionBannerSearchResponse;
import im.dangmoo.benefit.admin.dto.promotion.banner.PromotionBannerUpdateRequest;
import im.dangmoo.benefit.admin.dto.promotion.banner.PromotionBannerUpdateResponse;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionBannerChangeStatusUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionBannerCreateUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionBannerDetailUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionBannerSearchUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionBannerUpdateUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromotionBannerController {

    private final PromotionBannerSearchUseCase promotionBannerSearchUseCase;
    private final PromotionBannerDetailUseCase promotionBannerDetailUseCase;
    private final PromotionBannerCreateUseCase promotionBannerCreateUseCase;
    private final PromotionBannerUpdateUseCase promotionBannerUpdateUseCase;
    private final PromotionBannerChangeStatusUseCase promotionBannerChangeStatusUseCase;

    public PromotionBannerController(
        final PromotionBannerSearchUseCase promotionBannerSearchUseCase,
        final PromotionBannerDetailUseCase promotionBannerDetailUseCase,
        final PromotionBannerCreateUseCase promotionBannerCreateUseCase,
        final PromotionBannerUpdateUseCase promotionBannerUpdateUseCase,
        final PromotionBannerChangeStatusUseCase promotionBannerChangeStatusUseCase
    ) {
        this.promotionBannerSearchUseCase = promotionBannerSearchUseCase;
        this.promotionBannerDetailUseCase = promotionBannerDetailUseCase;
        this.promotionBannerCreateUseCase = promotionBannerCreateUseCase;
        this.promotionBannerUpdateUseCase = promotionBannerUpdateUseCase;
        this.promotionBannerChangeStatusUseCase = promotionBannerChangeStatusUseCase;
    }

    @GetMapping(AdminApiPath.PROMOTION_BANNERS)
    AdminApiResponse<PromotionBannerSearchResponse> search(
        @ModelAttribute final PromotionBannerSearchRequest request
    ) {
        return AdminApiResponse.of(promotionBannerSearchUseCase.search(request));
    }

    @GetMapping(AdminApiPath.PROMOTION_BANNER)
    AdminApiResponse<PromotionBannerDetailResponse> detail(@PathVariable final String id) {
        return AdminApiResponse.of(promotionBannerDetailUseCase.detail(id));
    }

    @PostMapping(AdminApiPath.PROMOTION_BANNERS)
    AdminApiResponse<PromotionBannerCreateResponse> create(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PromotionBannerCreateRequest request
    ) {
        return AdminApiResponse.of(promotionBannerCreateUseCase.create(adminId, request));
    }

    @PutMapping(AdminApiPath.PROMOTION_BANNER)
    AdminApiResponse<PromotionBannerUpdateResponse> update(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final PromotionBannerUpdateRequest request
    ) {
        return AdminApiResponse.of(promotionBannerUpdateUseCase.update(adminId, id, request));
    }

    @PutMapping(AdminApiPath.PROMOTION_BANNER_STATUS)
    AdminApiResponse<PromotionBannerChangeStatusResponse> changeStatus(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final PromotionBannerChangeStatusRequest request
    ) {
        return AdminApiResponse.of(promotionBannerChangeStatusUseCase.changeStatus(adminId, id, request));
    }
}

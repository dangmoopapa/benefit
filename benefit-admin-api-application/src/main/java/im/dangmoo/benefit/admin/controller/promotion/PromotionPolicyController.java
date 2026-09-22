package im.dangmoo.benefit.admin.controller.promotion;

import im.dangmoo.benefit.admin.controller.AdminApiHeaders;
import im.dangmoo.benefit.admin.controller.AdminApiPath;
import im.dangmoo.benefit.admin.controller.AdminApiResponse;
import im.dangmoo.benefit.admin.model.promotion.applier.PromotionApplierSearchResponse;
import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicyChangeStatusRequest;
import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicyChangeStatusResponse;
import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicyCreateRequest;
import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicyCreateResponse;
import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicyDetailResponse;
import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicySearchRequest;
import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicySearchResponse;
import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicyUpdateRequest;
import im.dangmoo.benefit.admin.model.promotion.policy.PromotionPolicyUpdateResponse;
import im.dangmoo.benefit.admin.model.promotion.winner.PromotionDrawRequest;
import im.dangmoo.benefit.admin.model.promotion.winner.PromotionWinnerListResponse;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionApplierSearchUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionDrawUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionPolicyChangeStatusUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionPolicyCreateUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionPolicyDetailUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionPolicySearchUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionPolicyUpdateUseCase;
import im.dangmoo.benefit.admin.usecase.promotion.PromotionWinnerSearchUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromotionPolicyController {

    private final PromotionPolicySearchUseCase promotionPolicySearchUseCase;
    private final PromotionPolicyDetailUseCase promotionPolicyDetailUseCase;
    private final PromotionPolicyCreateUseCase promotionPolicyCreateUseCase;
    private final PromotionPolicyUpdateUseCase promotionPolicyUpdateUseCase;
    private final PromotionPolicyChangeStatusUseCase promotionPolicyChangeStatusUseCase;
    private final PromotionApplierSearchUseCase promotionApplierSearchUseCase;
    private final PromotionWinnerSearchUseCase promotionWinnerSearchUseCase;
    private final PromotionDrawUseCase promotionDrawUseCase;

    public PromotionPolicyController(
        final PromotionPolicySearchUseCase promotionPolicySearchUseCase,
        final PromotionPolicyDetailUseCase promotionPolicyDetailUseCase,
        final PromotionPolicyCreateUseCase promotionPolicyCreateUseCase,
        final PromotionPolicyUpdateUseCase promotionPolicyUpdateUseCase,
        final PromotionPolicyChangeStatusUseCase promotionPolicyChangeStatusUseCase,
        final PromotionApplierSearchUseCase promotionApplierSearchUseCase,
        final PromotionWinnerSearchUseCase promotionWinnerSearchUseCase,
        final PromotionDrawUseCase promotionDrawUseCase
    ) {
        this.promotionPolicySearchUseCase = promotionPolicySearchUseCase;
        this.promotionPolicyDetailUseCase = promotionPolicyDetailUseCase;
        this.promotionPolicyCreateUseCase = promotionPolicyCreateUseCase;
        this.promotionPolicyUpdateUseCase = promotionPolicyUpdateUseCase;
        this.promotionPolicyChangeStatusUseCase = promotionPolicyChangeStatusUseCase;
        this.promotionApplierSearchUseCase = promotionApplierSearchUseCase;
        this.promotionWinnerSearchUseCase = promotionWinnerSearchUseCase;
        this.promotionDrawUseCase = promotionDrawUseCase;
    }

    @GetMapping(AdminApiPath.PROMOTION_POLICIES)
    AdminApiResponse<PromotionPolicySearchResponse> search(
        @ModelAttribute final PromotionPolicySearchRequest request
    ) {
        return AdminApiResponse.of(promotionPolicySearchUseCase.search(request));
    }

    @GetMapping(AdminApiPath.PROMOTION_POLICY)
    AdminApiResponse<PromotionPolicyDetailResponse> detail(@PathVariable final String id) {
        return AdminApiResponse.of(promotionPolicyDetailUseCase.detail(id));
    }

    @PostMapping(AdminApiPath.PROMOTION_POLICIES)
    AdminApiResponse<PromotionPolicyCreateResponse> create(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PromotionPolicyCreateRequest request
    ) {
        return AdminApiResponse.of(promotionPolicyCreateUseCase.create(adminId, request));
    }

    @PutMapping(AdminApiPath.PROMOTION_POLICY)
    AdminApiResponse<PromotionPolicyUpdateResponse> update(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final PromotionPolicyUpdateRequest request
    ) {
        return AdminApiResponse.of(promotionPolicyUpdateUseCase.update(adminId, id, request));
    }

    @PutMapping(AdminApiPath.PROMOTION_POLICY_STATUS)
    AdminApiResponse<PromotionPolicyChangeStatusResponse> changeStatus(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final PromotionPolicyChangeStatusRequest request
    ) {
        return AdminApiResponse.of(promotionPolicyChangeStatusUseCase.changeStatus(adminId, id, request));
    }

    @GetMapping(AdminApiPath.PROMOTION_POLICY_APPLIERS)
    AdminApiResponse<PromotionApplierSearchResponse> searchAppliers(@PathVariable final String id) {
        return AdminApiResponse.of(promotionApplierSearchUseCase.search(id));
    }

    @GetMapping(AdminApiPath.PROMOTION_POLICY_WINNERS)
    AdminApiResponse<PromotionWinnerListResponse> searchWinners(@PathVariable final String id) {
        return AdminApiResponse.of(promotionWinnerSearchUseCase.search(id));
    }

    @PostMapping(AdminApiPath.PROMOTION_POLICY_DRAW)
    AdminApiResponse<PromotionWinnerListResponse> draw(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final PromotionDrawRequest request
    ) {
        return AdminApiResponse.of(promotionDrawUseCase.draw(adminId, id, request));
    }
}

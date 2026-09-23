package im.dangmoo.benefit.api.controller.coupon;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.dto.coupon.VoucherCouponBoxRequest;
import im.dangmoo.benefit.api.dto.coupon.VoucherCouponBoxResponse;
import im.dangmoo.benefit.api.usecase.coupon.VoucherCouponBoxUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoucherCouponController {

    private final VoucherCouponBoxUseCase voucherCouponBoxUseCase;

    public VoucherCouponController(final VoucherCouponBoxUseCase voucherCouponBoxUseCase) {
        this.voucherCouponBoxUseCase = voucherCouponBoxUseCase;
    }

    @PostMapping(ApiPath.COUPON_VOUCHER_BOX)
    ApiResponse<VoucherCouponBoxResponse> box(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final VoucherCouponBoxRequest request
    ) {
        return ApiResponse.of(voucherCouponBoxUseCase.box(userId, request));
    }
}

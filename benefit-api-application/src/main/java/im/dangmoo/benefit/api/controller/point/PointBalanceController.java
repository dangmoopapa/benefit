package im.dangmoo.benefit.api.controller.point;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.model.point.PointBalanceResponse;
import im.dangmoo.benefit.api.usecase.point.PointBalanceUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointBalanceController {

    private final PointBalanceUseCase pointBalanceUseCase;

    public PointBalanceController(final PointBalanceUseCase pointBalanceUseCase) {
        this.pointBalanceUseCase = pointBalanceUseCase;
    }

    @GetMapping(ApiPath.POINT_BALANCES)
    ApiResponse<PointBalanceResponse> balance(@RequestHeader(ApiHeaders.USER_ID) final String userId) {
        return ApiResponse.of(pointBalanceUseCase.balance(userId));
    }
}

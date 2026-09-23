package im.dangmoo.benefit.api.controller.point;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.dto.point.PointBalanceResponse;
import im.dangmoo.benefit.api.dto.point.PointTransactionListResponse;
import im.dangmoo.benefit.api.dto.point.PointTransactionPageRequest;
import im.dangmoo.benefit.api.usecase.point.PointBalanceUseCase;
import im.dangmoo.benefit.api.usecase.point.PointTransactionSearchUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointBookController {

    private final PointBalanceUseCase pointBalanceUseCase;
    private final PointTransactionSearchUseCase pointTransactionSearchUseCase;

    public PointBookController(
        final PointBalanceUseCase pointBalanceUseCase,
        final PointTransactionSearchUseCase pointTransactionSearchUseCase
    ) {
        this.pointBalanceUseCase = pointBalanceUseCase;
        this.pointTransactionSearchUseCase = pointTransactionSearchUseCase;
    }

    @GetMapping(ApiPath.POINT_BALANCES)
    ApiResponse<PointBalanceResponse> balance(
        @RequestHeader(ApiHeaders.USER_ID) final String userId
    ) {
        return ApiResponse.of(pointBalanceUseCase.balance(userId));
    }

    @GetMapping(ApiPath.POINT_TRANSACTIONS)
    ApiResponse<PointTransactionListResponse> transactions(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @ModelAttribute final PointTransactionPageRequest request
    ) {
        return ApiResponse.of(pointTransactionSearchUseCase.search(userId, request));
    }
}

package im.dangmoo.benefit.api.web.point.controller;

import im.dangmoo.benefit.api.support.ApiResponse;
import im.dangmoo.benefit.api.support.UserHeaders;
import im.dangmoo.benefit.api.web.ApiPath;
import im.dangmoo.benefit.api.web.point.model.PointBookResponse;
import im.dangmoo.benefit.api.web.point.model.PointTransactionResponse;
import im.dangmoo.benefit.api.web.point.service.PointBookService;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PointBookController {

    private final PointBookService pointBookService;

    public PointBookController(final PointBookService pointBookService) {
        this.pointBookService = pointBookService;
    }

    @GetMapping(ApiPath.POINT_BOOK_BALANCES)
    ApiResponse<PointBookResponse> getBalance(@RequestHeader(UserHeaders.USER_ID) final String userId) {
        return ApiResponse.of(pointBookService.getBalance(userId));
    }

    @GetMapping(ApiPath.POINT_BOOK_TRANSACTIONS)
    ApiResponse<List<PointTransactionResponse>> getTransactions(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @RequestParam(required = false) final List<PointTransactionType> types
    ) {
        return ApiResponse.of(pointBookService.getTransactions(userId, types));
    }
}

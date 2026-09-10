package im.dangmoo.benefit.admin.web.point.controller;

import im.dangmoo.benefit.admin.support.ApiResponse;
import im.dangmoo.benefit.admin.web.AdminApiPath;
import im.dangmoo.benefit.admin.web.point.model.PointBalanceResponse;
import im.dangmoo.benefit.admin.web.point.service.PointBalanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointBalanceController {

    private final PointBalanceService pointBalanceService;

    public PointBalanceController(final PointBalanceService pointBalanceService) {
        this.pointBalanceService = pointBalanceService;
    }

    @GetMapping(AdminApiPath.POINT_BALANCES)
    ApiResponse<PointBalanceResponse> get(@RequestParam final String userId) {
        return ApiResponse.of(pointBalanceService.get(userId));
    }
}

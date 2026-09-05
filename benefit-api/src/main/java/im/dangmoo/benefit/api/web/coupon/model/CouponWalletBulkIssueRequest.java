package im.dangmoo.benefit.api.web.coupon.model;

import java.util.List;

public record CouponWalletBulkIssueRequest(
    List<CouponWalletIssueRequest> items
) {
}

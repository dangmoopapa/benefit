package im.dangmoo.benefit.admin.web.coupon.model;

import java.util.List;

public record CouponWalletIssueRequest(
    String userId,
    String policyId,
    boolean enforceIssueCondition,
    List<String> segmentIds
) {
}

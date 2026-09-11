package im.dangmoo.benefit.api.web.coupon.service;

import im.dangmoo.benefit.api.support.ApiException;
import im.dangmoo.benefit.api.support.ApiMessage;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletIssueAvailabilityResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.domain.component.coupon.issue.CouponIssue;
import im.dangmoo.benefit.domain.component.coupon.issue.CouponIssueAvailability;
import im.dangmoo.benefit.domain.component.coupon.issue.CouponIssuer;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CouponIssueService {

    private final CouponIssuer couponIssuer;

    public CouponIssueService(final CouponIssuer couponIssuer) {
        this.couponIssuer = couponIssuer;
    }

    public CouponWalletIssueAvailabilityResponse check(
        final String userId,
        final Collection<String> userSegmentIds,
        final CouponWalletIssueRequest request
    ) {
        final CouponIssueAvailability availability = couponIssuer.check(
            userId,
            request.policyId(),
            true,
            userSegmentIds
        );
        return new CouponWalletIssueAvailabilityResponse(
            availability.issuable(),
            availability.reason().name(),
            availability.policyActive(),
            availability.issueOpen(),
            availability.quantityRemaining(),
            availability.alreadyIssued(),
            availability.totalQuantity(),
            availability.issuedCount()
        );
    }

    public CouponWalletResponse issue(
        final String userId,
        final Collection<String> userSegmentIds,
        final CouponWalletIssueRequest request
    ) {
        return switch (couponIssuer.issue(
            userId,
            request.policyId(),
            true,
            userSegmentIds,
            userId
        )) {
            case CouponIssue.Success success -> CouponWalletResponse.of(success);
            case CouponIssue.PolicyNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case CouponIssue.PolicyNotActive _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case CouponIssue.NotAllowed _ -> throw new ApiException(ApiMessage.ISSUE_NOT_ALLOWED);
            case CouponIssue.AlreadyIssued _ -> throw new ApiException(ApiMessage.ALREADY_ISSUED);
        };
    }
}

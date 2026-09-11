package im.dangmoo.benefit.api.web.membership.service;

import im.dangmoo.benefit.api.support.ApiException;
import im.dangmoo.benefit.api.support.ApiMessage;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.api.web.membership.model.MembershipPrivilegeApplyRequest;
import im.dangmoo.benefit.api.web.membership.model.MembershipPrivilegeApplyResponse;
import im.dangmoo.benefit.domain.component.coupon.issue.CouponIssue;
import im.dangmoo.benefit.domain.component.membership.MembershipCouponIssuer;
import im.dangmoo.benefit.domain.component.membership.MembershipPaymentApplier;
import im.dangmoo.benefit.domain.component.membership.MembershipSubscriber;
import im.dangmoo.benefit.domain.component.point.issue.PointIssue;
import im.dangmoo.benefit.domain.data.membership.subscription.MembershipSubscription;
import org.springframework.stereotype.Service;

@Service
public class MembershipPrivilegeService {

    private final MembershipSubscriber membershipSubscriber;
    private final MembershipPaymentApplier membershipPaymentApplier;
    private final MembershipCouponIssuer membershipCouponIssuer;

    public MembershipPrivilegeService(
        final MembershipSubscriber membershipSubscriber,
        final MembershipPaymentApplier membershipPaymentApplier,
        final MembershipCouponIssuer membershipCouponIssuer
    ) {
        this.membershipSubscriber = membershipSubscriber;
        this.membershipPaymentApplier = membershipPaymentApplier;
        this.membershipCouponIssuer = membershipCouponIssuer;
    }

    public MembershipPrivilegeApplyResponse apply(
        final String userId,
        final MembershipPrivilegeApplyRequest request
    ) {
        final MembershipSubscription subscription = membershipSubscriber.current(userId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_MEMBER));
        return membershipPaymentApplier.apply(
                subscription,
                request.amount(),
                request.orderId(),
                userId
            )
            .map(this::toResponse)
            .orElseGet(MembershipPrivilegeApplyResponse::skipped);
    }

    public CouponWalletResponse issueCoupon(final String userId) {
        final MembershipSubscription subscription = membershipSubscriber.current(userId)
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_MEMBER));
        return switch (membershipCouponIssuer.issue(subscription, userId)) {
            case CouponIssue.Success issued -> CouponWalletResponse.of(issued);
            case CouponIssue.AlreadyIssued _ -> throw new ApiException(ApiMessage.ALREADY_ISSUED);
            case CouponIssue.PolicyNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case CouponIssue.PolicyNotActive _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case CouponIssue.NotAllowed _ -> throw new ApiException(ApiMessage.ISSUE_NOT_ALLOWED);
        };
    }

    private MembershipPrivilegeApplyResponse toResponse(final PointIssue issued) {
        return switch (issued) {
            case PointIssue.Success success -> MembershipPrivilegeApplyResponse.of(success);
            case PointIssue.PolicyNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case PointIssue.PolicyNotActive _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case PointIssue.InvalidAmount _ -> throw new ApiException(ApiMessage.INVALID_AMOUNT);
        };
    }
}

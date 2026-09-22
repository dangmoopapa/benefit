package im.dangmoo.benefit.api.controller.membership;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.model.coupon.CouponIssueResponse;
import im.dangmoo.benefit.api.model.membership.MembershipBenefitApplyRequest;
import im.dangmoo.benefit.api.model.membership.MembershipBenefitCancelRequest;
import im.dangmoo.benefit.api.model.membership.MembershipBenefitHistoryListResponse;
import im.dangmoo.benefit.api.model.membership.MembershipBenefitHistoryPageRequest;
import im.dangmoo.benefit.api.model.membership.MembershipBenefitHistoryResponse;
import im.dangmoo.benefit.api.usecase.membership.MembershipBenefitApplyUseCase;
import im.dangmoo.benefit.api.usecase.membership.MembershipBenefitCancelUseCase;
import im.dangmoo.benefit.api.usecase.membership.MembershipBenefitHistoryListUseCase;
import im.dangmoo.benefit.api.usecase.membership.MembershipBenefitCouponIssueUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MembershipBenefitController {

    private final MembershipBenefitApplyUseCase membershipBenefitApplyUseCase;
    private final MembershipBenefitCancelUseCase membershipBenefitCancelUseCase;
    private final MembershipBenefitHistoryListUseCase membershipBenefitHistoryListUseCase;
    private final MembershipBenefitCouponIssueUseCase membershipBenefitCouponIssueUseCase;

    public MembershipBenefitController(
        final MembershipBenefitApplyUseCase membershipBenefitApplyUseCase,
        final MembershipBenefitCancelUseCase membershipBenefitCancelUseCase,
        final MembershipBenefitHistoryListUseCase membershipBenefitHistoryListUseCase,
        final MembershipBenefitCouponIssueUseCase membershipBenefitCouponIssueUseCase
    ) {
        this.membershipBenefitApplyUseCase = membershipBenefitApplyUseCase;
        this.membershipBenefitCancelUseCase = membershipBenefitCancelUseCase;
        this.membershipBenefitHistoryListUseCase = membershipBenefitHistoryListUseCase;
        this.membershipBenefitCouponIssueUseCase = membershipBenefitCouponIssueUseCase;
    }

    @PostMapping(ApiPath.MEMBERSHIP_BENEFIT_APPLY)
    ApiResponse<MembershipBenefitHistoryResponse> apply(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final MembershipBenefitApplyRequest request
    ) {
        return ApiResponse.of(membershipBenefitApplyUseCase.apply(userId, request));
    }

    @PostMapping(ApiPath.MEMBERSHIP_BENEFIT_CANCEL)
    ApiResponse<MembershipBenefitHistoryResponse> cancel(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final MembershipBenefitCancelRequest request
    ) {
        return ApiResponse.of(membershipBenefitCancelUseCase.cancel(userId, request));
    }

    @GetMapping(ApiPath.MEMBERSHIP_BENEFIT_HISTORIES)
    ApiResponse<MembershipBenefitHistoryListResponse> histories(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @ModelAttribute final MembershipBenefitHistoryPageRequest request
    ) {
        return ApiResponse.of(membershipBenefitHistoryListUseCase.list(userId, request));
    }

    @PostMapping(ApiPath.MEMBERSHIP_BENEFIT_COUPON_ISSUE)
    ApiResponse<CouponIssueResponse> issueCoupon(
        @RequestHeader(ApiHeaders.USER_ID) final String userId
    ) {
        return ApiResponse.of(membershipBenefitCouponIssueUseCase.issue(userId));
    }
}

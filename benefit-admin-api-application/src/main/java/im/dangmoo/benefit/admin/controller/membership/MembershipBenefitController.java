package im.dangmoo.benefit.admin.controller.membership;

import im.dangmoo.benefit.admin.controller.AdminApiPath;
import im.dangmoo.benefit.admin.controller.AdminApiResponse;
import im.dangmoo.benefit.admin.model.membership.history.MembershipBenefitHistorySearchRequest;
import im.dangmoo.benefit.admin.model.membership.history.MembershipBenefitHistorySearchResponse;
import im.dangmoo.benefit.admin.usecase.membership.MembershipBenefitHistorySearchUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MembershipBenefitController {

    private final MembershipBenefitHistorySearchUseCase membershipBenefitHistorySearchUseCase;

    public MembershipBenefitController(
        final MembershipBenefitHistorySearchUseCase membershipBenefitHistorySearchUseCase
    ) {
        this.membershipBenefitHistorySearchUseCase = membershipBenefitHistorySearchUseCase;
    }

    @GetMapping(AdminApiPath.MEMBERSHIP_BENEFIT_HISTORIES)
    AdminApiResponse<MembershipBenefitHistorySearchResponse> histories(
        @ModelAttribute final MembershipBenefitHistorySearchRequest request
    ) {
        return AdminApiResponse.of(membershipBenefitHistorySearchUseCase.search(request));
    }
}

package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.model.membership.history.MembershipBenefitHistorySearchRequest;
import im.dangmoo.benefit.admin.model.membership.history.MembershipBenefitHistorySearchResponse;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistoryMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipBenefitHistorySearchUseCase {

    private final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository;

    public MembershipBenefitHistorySearchUseCase(
        final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository
    ) {
        this.membershipBenefitHistoryMongoRepository = membershipBenefitHistoryMongoRepository;
    }

    public MembershipBenefitHistorySearchResponse execute(
        final MembershipBenefitHistorySearchRequest request
    ) {
        final int page = request.pageOrDefault();
        final int size = request.sizeOrDefault();
        return MembershipBenefitHistorySearchResponse.of(
            membershipBenefitHistoryMongoRepository.search(
                request.userId(),
                request.contractId(),
                page,
                size
            ),
            membershipBenefitHistoryMongoRepository.count(request.userId(), request.contractId()),
            page,
            size
        );
    }
}

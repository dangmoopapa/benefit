package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.dto.membership.history.MembershipBenefitHistorySearchRequest;
import im.dangmoo.benefit.admin.dto.membership.history.MembershipBenefitHistorySearchResponse;
import im.dangmoo.benefit.data.infrastructure.membership.MembershipBenefitHistoryMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipBenefitHistorySearchUseCase {

    private final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository;

    public MembershipBenefitHistorySearchUseCase(
        final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository
    ) {
        this.membershipBenefitHistoryMongoRepository = membershipBenefitHistoryMongoRepository;
    }

    public MembershipBenefitHistorySearchResponse search(
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

package im.dangmoo.benefit.api.usecase.membership;

import im.dangmoo.benefit.api.model.membership.MembershipBenefitHistoryListResponse;
import im.dangmoo.benefit.api.model.membership.MembershipBenefitHistoryPageRequest;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistoryMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipBenefitHistoryListUseCase {

    private final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository;

    public MembershipBenefitHistoryListUseCase(
        final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository
    ) {
        this.membershipBenefitHistoryMongoRepository = membershipBenefitHistoryMongoRepository;
    }

    public MembershipBenefitHistoryListResponse execute(
        final String userId,
        final MembershipBenefitHistoryPageRequest request
    ) {
        final int page = request.pageOrDefault();
        final int size = request.sizeOrDefault();
        return MembershipBenefitHistoryListResponse.of(
            membershipBenefitHistoryMongoRepository.search(userId, null, page, size),
            membershipBenefitHistoryMongoRepository.count(userId, null),
            page,
            size
        );
    }
}

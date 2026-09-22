package im.dangmoo.benefit.api.usecase.membership;

import im.dangmoo.benefit.api.model.membership.MembershipBenefitCancelRequest;
import im.dangmoo.benefit.api.model.membership.MembershipBenefitHistoryResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistoryStatus;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistoryMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipBenefitCancelUseCase {

    private final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository;

    public MembershipBenefitCancelUseCase(
        final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository
    ) {
        this.membershipBenefitHistoryMongoRepository = membershipBenefitHistoryMongoRepository;
    }

    public MembershipBenefitHistoryResponse execute(
        final String userId,
        final MembershipBenefitCancelRequest request
    ) {
        final var history = membershipBenefitHistoryMongoRepository.findByOrderId(request.orderId())
            .orElseThrow(ApiException::notFound);
        if (!userId.equals(history.getUserId())) {
            throw ApiException.notFound();
        }
        if (history.getStatus() == MembershipBenefitHistoryStatus.CANCELLED) {
            return MembershipBenefitHistoryResponse.of(history);
        }
        history.cancel(userId);
        return MembershipBenefitHistoryResponse.of(
            membershipBenefitHistoryMongoRepository.save(history)
        );
    }
}

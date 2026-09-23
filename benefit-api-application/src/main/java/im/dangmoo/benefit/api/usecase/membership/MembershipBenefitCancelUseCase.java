package im.dangmoo.benefit.api.usecase.membership;

import im.dangmoo.benefit.api.dto.membership.MembershipBenefitCancelRequest;
import im.dangmoo.benefit.api.dto.membership.MembershipBenefitHistoryResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.data.entity.membership.history.MembershipBenefitHistoryStatus;
import im.dangmoo.benefit.data.infrastructure.membership.MembershipBenefitHistoryMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipBenefitCancelUseCase {

    private final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository;

    public MembershipBenefitCancelUseCase(
        final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository
    ) {
        this.membershipBenefitHistoryMongoRepository = membershipBenefitHistoryMongoRepository;
    }

    public MembershipBenefitHistoryResponse cancel(
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
        final var saved = membershipBenefitHistoryMongoRepository.save(history.cancel(userId));
        return MembershipBenefitHistoryResponse.of(saved);
    }
}

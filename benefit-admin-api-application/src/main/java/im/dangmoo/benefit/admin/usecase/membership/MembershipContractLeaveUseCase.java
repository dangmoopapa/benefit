package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractLeaveRequest;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MembershipContractLeaveUseCase {

    private final MembershipContractMongoRepository membershipContractMongoRepository;

    public MembershipContractLeaveUseCase(
        final MembershipContractMongoRepository membershipContractMongoRepository
    ) {
        this.membershipContractMongoRepository = membershipContractMongoRepository;
    }

    public MembershipContractResponse leave(
        final String adminId,
        final MembershipContractLeaveRequest request
    ) {
        final Instant now = Instant.now();
        final var contract = membershipContractMongoRepository
            .findEffectiveByUserId(request.userId(), now)
            .orElseThrow(ApiException::notFound);
        final var saved = membershipContractMongoRepository.save(contract.scheduleCancel(adminId));
        return MembershipContractResponse.of(saved);
    }
}

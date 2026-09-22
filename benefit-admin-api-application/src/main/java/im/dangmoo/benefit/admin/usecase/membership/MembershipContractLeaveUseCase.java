package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractLeaveRequest;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.membership.MembershipContractDomain;
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

    public MembershipContractResponse execute(
        final String adminId,
        final MembershipContractLeaveRequest request
    ) {
        final Instant now = Instant.now();
        final var contract = membershipContractMongoRepository
            .findEffectiveByUserId(request.userId(), now)
            .orElseThrow(ApiException::notFound);
        if (!MembershipContractDomain.isEffective(contract, now)) {
            throw ApiException.invalidStatus();
        }
        contract.scheduleCancel(adminId);
        return MembershipContractResponse.of(
            membershipContractMongoRepository.save(contract)
        );
    }
}

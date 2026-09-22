package im.dangmoo.benefit.api.usecase.membership;

import im.dangmoo.benefit.api.model.membership.MembershipContractResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
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

    public MembershipContractResponse execute(final String userId) {
        final Instant now = Instant.now();
        final var contract = membershipContractMongoRepository
            .findEffectiveByUserId(userId, now)
            .orElseThrow(ApiException::notFound);
        if (!MembershipContractDomain.isEffective(contract, now)) {
            throw ApiException.invalidStatus();
        }
        contract.scheduleCancel(userId);
        return MembershipContractResponse.of(
            membershipContractMongoRepository.save(contract)
        );
    }
}

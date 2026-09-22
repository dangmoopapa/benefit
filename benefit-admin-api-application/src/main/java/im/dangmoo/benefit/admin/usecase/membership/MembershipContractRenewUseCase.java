package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractRenewRequest;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.membership.MembershipContractDomain;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MembershipContractRenewUseCase {

    private final MembershipContractMongoRepository membershipContractMongoRepository;

    public MembershipContractRenewUseCase(
        final MembershipContractMongoRepository membershipContractMongoRepository
    ) {
        this.membershipContractMongoRepository = membershipContractMongoRepository;
    }

    public MembershipContractResponse execute(
        final String adminId,
        final MembershipContractRenewRequest request
    ) {
        final Instant now = Instant.now();
        final var contract = membershipContractMongoRepository
            .findEffectiveByUserId(request.userId(), now)
            .orElseThrow(ApiException::notFound);
        if (contract.isCancelAtPeriodEnd() || !contract.isAutoRenew()) {
            throw ApiException.invalidStatus();
        }
        final Instant periodEnd = MembershipContractDomain.nextPeriodEnd(contract.getPeriodEnd());
        contract.renew(periodEnd, adminId);
        return MembershipContractResponse.of(
            membershipContractMongoRepository.save(contract)
        );
    }
}

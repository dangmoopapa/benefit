package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractSearchRequest;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractSearchResponse;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipContractSearchUseCase {

    private final MembershipContractMongoRepository membershipContractMongoRepository;

    public MembershipContractSearchUseCase(
        final MembershipContractMongoRepository membershipContractMongoRepository
    ) {
        this.membershipContractMongoRepository = membershipContractMongoRepository;
    }

    public MembershipContractSearchResponse search(
        final MembershipContractSearchRequest request
    ) {
        return MembershipContractSearchResponse.of(
            membershipContractMongoRepository.search(
                request.userId(),
                request.policyId(),
                request.status()
            )
        );
    }
}

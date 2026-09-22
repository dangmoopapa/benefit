package im.dangmoo.benefit.api.usecase.membership;

import im.dangmoo.benefit.api.model.membership.MembershipContractResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MembershipContractDetailUseCase {

    private final MembershipContractMongoRepository membershipContractMongoRepository;

    public MembershipContractDetailUseCase(
        final MembershipContractMongoRepository membershipContractMongoRepository
    ) {
        this.membershipContractMongoRepository = membershipContractMongoRepository;
    }

    public MembershipContractResponse execute(final String userId) {
        return membershipContractMongoRepository
            .findEffectiveByUserId(userId, Instant.now())
            .map(MembershipContractResponse::of)
            .orElseThrow(ApiException::notFound);
    }
}

package im.dangmoo.benefit.api.usecase.membership;

import im.dangmoo.benefit.api.dto.membership.MembershipContractResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.data.infrastructure.membership.MembershipContractMongoRepository;
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

    public MembershipContractResponse detail(final String userId) {
        return membershipContractMongoRepository
            .findEffectiveByUserId(userId, Instant.now())
            .map(MembershipContractResponse::of)
            .orElseThrow(ApiException::notFound);
    }
}

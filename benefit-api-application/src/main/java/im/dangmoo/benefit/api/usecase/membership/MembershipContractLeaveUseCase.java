package im.dangmoo.benefit.api.usecase.membership;

import im.dangmoo.benefit.api.dto.membership.MembershipContractResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.data.infrastructure.membership.MembershipContractMongoRepository;
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

    public MembershipContractResponse leave(final String userId) {
        final Instant now = Instant.now();
        final var contract = membershipContractMongoRepository
            .findEffectiveByUserId(userId, now)
            .orElseThrow(ApiException::notFound);
        final var saved = membershipContractMongoRepository.save(contract.scheduleCancel(userId));
        return MembershipContractResponse.of(saved);
    }
}

package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.dto.membership.policy.MembershipPolicyUpdateRequest;
import im.dangmoo.benefit.admin.dto.membership.policy.MembershipPolicyUpdateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.membership.MembershipBenefitDomain;
import im.dangmoo.benefit.data.infrastructure.membership.MembershipPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipPolicyUpdateUseCase {

    private final MembershipPolicyMongoRepository membershipPolicyMongoRepository;

    public MembershipPolicyUpdateUseCase(
        final MembershipPolicyMongoRepository membershipPolicyMongoRepository
    ) {
        this.membershipPolicyMongoRepository = membershipPolicyMongoRepository;
    }

    public MembershipPolicyUpdateResponse update(
        final String adminId,
        final String id,
        final MembershipPolicyUpdateRequest request
    ) {
        final var policy = membershipPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        if (!MembershipBenefitDomain.of(request.season(), request.benefit()).isServiceable()) {
            throw ApiException.preparingMembership();
        }
        final var saved = membershipPolicyMongoRepository.save(
            policy.update(
                request.name(),
                request.description(),
                request.season(),
                request.benefit(),
                request.accountCondition().toDocument(),
                adminId
            )
        );
        return MembershipPolicyUpdateResponse.of(saved);
    }
}

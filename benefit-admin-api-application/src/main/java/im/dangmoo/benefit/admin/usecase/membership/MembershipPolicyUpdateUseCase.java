package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyUpdateRequest;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyUpdateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.membership.MembershipBenefitDomain;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipPolicyUpdateUseCase {

    private final MembershipPolicyMongoRepository membershipPolicyMongoRepository;

    public MembershipPolicyUpdateUseCase(
        final MembershipPolicyMongoRepository membershipPolicyMongoRepository
    ) {
        this.membershipPolicyMongoRepository = membershipPolicyMongoRepository;
    }

    public MembershipPolicyUpdateResponse execute(
        final String adminId,
        final String id,
        final MembershipPolicyUpdateRequest request
    ) {
        final var policy = membershipPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        try {
            MembershipBenefitDomain.requireReady(request.season(), request.benefit());
        } catch (final MembershipBenefitDomain.PreparingException ex) {
            throw ApiException.preparingMembership();
        }
        policy.update(
            request.name(),
            request.description(),
            request.season(),
            request.benefit(),
            request.accountCondition().toDocument(),
            adminId
        );
        return MembershipPolicyUpdateResponse.of(membershipPolicyMongoRepository.save(policy));
    }
}

package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyCreateRequest;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyCreateResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.membership.MembershipBenefitDomain;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipPolicyCreateUseCase {

    private final MembershipPolicyMongoRepository membershipPolicyMongoRepository;

    public MembershipPolicyCreateUseCase(
        final MembershipPolicyMongoRepository membershipPolicyMongoRepository
    ) {
        this.membershipPolicyMongoRepository = membershipPolicyMongoRepository;
    }

    public MembershipPolicyCreateResponse create(
        final String adminId,
        final MembershipPolicyCreateRequest request
    ) {
        if (membershipPolicyMongoRepository.existsByKey(request.key())) {
            throw ApiException.duplicateKey();
        }
        try {
            MembershipBenefitDomain.requireReady(request.season(), request.benefit());
        } catch (final MembershipBenefitDomain.PreparingException ex) {
            throw ApiException.preparingMembership();
        }
        final var saved = membershipPolicyMongoRepository.save(request.toDocument(adminId));
        return MembershipPolicyCreateResponse.of(saved);
    }
}

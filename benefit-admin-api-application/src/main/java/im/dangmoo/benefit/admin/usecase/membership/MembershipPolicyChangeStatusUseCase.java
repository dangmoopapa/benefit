package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.dto.membership.policy.MembershipPolicyChangeStatusRequest;
import im.dangmoo.benefit.admin.dto.membership.policy.MembershipPolicyChangeStatusResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.data.infrastructure.membership.MembershipPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipPolicyChangeStatusUseCase {

    private final MembershipPolicyMongoRepository membershipPolicyMongoRepository;

    public MembershipPolicyChangeStatusUseCase(
        final MembershipPolicyMongoRepository membershipPolicyMongoRepository
    ) {
        this.membershipPolicyMongoRepository = membershipPolicyMongoRepository;
    }

    public MembershipPolicyChangeStatusResponse changeStatus(
        final String adminId,
        final String id,
        final MembershipPolicyChangeStatusRequest request
    ) {
        final var policy = membershipPolicyMongoRepository.findById(id)
            .orElseThrow(ApiException::notFound);
        final var saved = membershipPolicyMongoRepository.save(
            policy.changeStatus(request.status(), adminId)
        );
        return MembershipPolicyChangeStatusResponse.of(saved);
    }
}

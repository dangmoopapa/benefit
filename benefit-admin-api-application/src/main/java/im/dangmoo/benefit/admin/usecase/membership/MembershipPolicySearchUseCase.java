package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicySearchRequest;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicySearchResponse;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MembershipPolicySearchUseCase {

    private final MembershipPolicyMongoRepository membershipPolicyMongoRepository;

    public MembershipPolicySearchUseCase(
        final MembershipPolicyMongoRepository membershipPolicyMongoRepository
    ) {
        this.membershipPolicyMongoRepository = membershipPolicyMongoRepository;
    }

    public MembershipPolicySearchResponse search(final MembershipPolicySearchRequest request) {
        return MembershipPolicySearchResponse.of(
            membershipPolicyMongoRepository.search(
                request.key(),
                request.name(),
                request.status(),
                request.season()
            )
        );
    }
}

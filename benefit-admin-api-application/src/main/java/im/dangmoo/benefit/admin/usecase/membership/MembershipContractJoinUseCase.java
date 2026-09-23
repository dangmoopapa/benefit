package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractJoinRequest;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.membership.MembershipBenefitDomain;
import im.dangmoo.benefit.domain.membership.MembershipContractDomain;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractDocument;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MembershipContractJoinUseCase {

    private final MembershipPolicyMongoRepository membershipPolicyMongoRepository;
    private final MembershipContractMongoRepository membershipContractMongoRepository;

    public MembershipContractJoinUseCase(
        final MembershipPolicyMongoRepository membershipPolicyMongoRepository,
        final MembershipContractMongoRepository membershipContractMongoRepository
    ) {
        this.membershipPolicyMongoRepository = membershipPolicyMongoRepository;
        this.membershipContractMongoRepository = membershipContractMongoRepository;
    }

    public MembershipContractResponse join(
        final String adminId,
        final MembershipContractJoinRequest request
    ) {
        final Instant now = Instant.now();
        if (membershipContractMongoRepository.findEffectiveByUserId(request.userId(), now).isPresent()) {
            throw ApiException.invalidStatus();
        }

        final MembershipPolicyDocument policy = membershipPolicyMongoRepository.findByKey(request.policyKey())
            .orElseThrow(ApiException::notFound);
        if (policy.getStatus().isNotActive()) {
            throw ApiException.invalidStatus();
        }
        if (!MembershipBenefitDomain.of(policy).isServiceable()) {
            throw ApiException.preparingMembership();
        }

        final MembershipContractDomain membershipContract =
            MembershipContractDomain.joining(policy.getId(), request.userId(), now);
        final Instant periodEnd = membershipContract.periodEnd();
        final String idempotencyKey = membershipContract.contractKey();
        final var existing = membershipContractMongoRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            final var saved = membershipContractMongoRepository.save(
                existing.get().reactivate(now, periodEnd, adminId)
            );
            return MembershipContractResponse.of(saved);
        }

        final var saved = membershipContractMongoRepository.save(
            MembershipContractDocument.join(
                request.userId(),
                policy.getId(),
                policy.getKey(),
                policy.getSeason(),
                now,
                periodEnd,
                idempotencyKey,
                adminId
            )
        );
        return MembershipContractResponse.of(saved);
    }
}

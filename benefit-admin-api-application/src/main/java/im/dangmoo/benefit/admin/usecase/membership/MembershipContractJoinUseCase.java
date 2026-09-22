package im.dangmoo.benefit.admin.usecase.membership;

import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractJoinRequest;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.membership.MembershipBenefitDomain;
import im.dangmoo.benefit.domain.membership.MembershipContractDomain;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicy;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContract;
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

        final MembershipPolicy policy = membershipPolicyMongoRepository.findByKey(request.policyKey())
            .orElseThrow(ApiException::notFound);
        if (policy.getStatus().isNotActive()) {
            throw ApiException.invalidStatus();
        }
        try {
            MembershipBenefitDomain.requireReady(policy.getSeason(), policy.getBenefit());
        } catch (final MembershipBenefitDomain.PreparingException ex) {
            throw ApiException.preparingMembership();
        }

        final Instant periodEnd = MembershipContractDomain.nextPeriodEnd(now);
        final String idempotencyKey = MembershipContractDomain.idempotencyKey(policy.getId(), request.userId());
        final var existing = membershipContractMongoRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            final var saved = membershipContractMongoRepository.save(
                existing.get().reactivate(now, periodEnd, adminId)
            );
            return MembershipContractResponse.of(saved);
        }

        final var saved = membershipContractMongoRepository.save(
            MembershipContract.join(
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

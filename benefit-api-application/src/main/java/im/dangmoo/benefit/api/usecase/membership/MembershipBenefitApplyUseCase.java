package im.dangmoo.benefit.api.usecase.membership;

import im.dangmoo.benefit.api.model.membership.MembershipBenefitApplyRequest;
import im.dangmoo.benefit.api.model.membership.MembershipBenefitHistoryResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.membership.MembershipBenefitDomain;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistory;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistoryMongoRepository;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MembershipBenefitApplyUseCase {

    private final MembershipContractMongoRepository membershipContractMongoRepository;
    private final MembershipPolicyMongoRepository membershipPolicyMongoRepository;
    private final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository;
    private final PointBalanceMongoRepository pointBalanceMongoRepository;

    public MembershipBenefitApplyUseCase(
        final MembershipContractMongoRepository membershipContractMongoRepository,
        final MembershipPolicyMongoRepository membershipPolicyMongoRepository,
        final MembershipBenefitHistoryMongoRepository membershipBenefitHistoryMongoRepository,
        final PointBalanceMongoRepository pointBalanceMongoRepository
    ) {
        this.membershipContractMongoRepository = membershipContractMongoRepository;
        this.membershipPolicyMongoRepository = membershipPolicyMongoRepository;
        this.membershipBenefitHistoryMongoRepository = membershipBenefitHistoryMongoRepository;
        this.pointBalanceMongoRepository = pointBalanceMongoRepository;
    }

    public MembershipBenefitHistoryResponse apply(
        final String userId,
        final MembershipBenefitApplyRequest request
    ) {
        final var existing = membershipBenefitHistoryMongoRepository.findByOrderId(request.orderId());
        if (existing.isPresent()) {
            return MembershipBenefitHistoryResponse.of(existing.get());
        }

        final Instant now = Instant.now();
        final var contract = membershipContractMongoRepository
            .findEffectiveByUserId(userId, now)
            .orElseThrow(ApiException::notFound);

        final var policy = membershipPolicyMongoRepository.findById(contract.getPolicyId())
            .orElseThrow(ApiException::notFound);
        try {
            MembershipBenefitDomain.requireReady(policy.getSeason(), policy.getBenefit());
        } catch (final MembershipBenefitDomain.PreparingException ex) {
            throw ApiException.preparingMembership();
        }

        final var applied = policy.getBenefit().apply(request.paymentAmount(), request.categoryId());
        if (applied.getCashbackAmount() != null && applied.getCashbackAmount().signum() > 0) {
            pointBalanceMongoRepository.increase(
                userId,
                PointBalance.NEVER_EXPIRES_AT,
                applied.getCashbackAmount().longValue()
            );
        }

        final MembershipBenefitHistory history = MembershipBenefitHistory.apply(
            userId,
            request.orderId(),
            contract.getId(),
            policy.getId(),
            policy.getSeason(),
            applied,
            userId
        );
        try {
            return MembershipBenefitHistoryResponse.of(
                membershipBenefitHistoryMongoRepository.save(history)
            );
        } catch (final DuplicateKeyException ex) {
            return MembershipBenefitHistoryResponse.of(
                membershipBenefitHistoryMongoRepository.findByOrderId(request.orderId()).orElseThrow()
            );
        }
    }
}

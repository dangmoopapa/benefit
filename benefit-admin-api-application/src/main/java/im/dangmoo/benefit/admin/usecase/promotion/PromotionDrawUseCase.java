package im.dangmoo.benefit.admin.usecase.promotion;

import im.dangmoo.benefit.admin.model.promotion.winner.PromotionDrawRequest;
import im.dangmoo.benefit.admin.model.promotion.winner.PromotionWinnerListResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.promotion.PromotionEntryDomain;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplierDocument;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplierMongoRepository;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.promotion.winner.PromotionWinnerDocument;
import im.dangmoo.benefit.infrastructure.data.promotion.winner.PromotionWinnerMongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PromotionDrawUseCase {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;
    private final PromotionApplierMongoRepository promotionApplierMongoRepository;
    private final PromotionWinnerMongoRepository promotionWinnerMongoRepository;

    public PromotionDrawUseCase(
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository,
        final PromotionApplierMongoRepository promotionApplierMongoRepository,
        final PromotionWinnerMongoRepository promotionWinnerMongoRepository
    ) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
        this.promotionApplierMongoRepository = promotionApplierMongoRepository;
        this.promotionWinnerMongoRepository = promotionWinnerMongoRepository;
    }

    public PromotionWinnerListResponse draw(
        final String adminId,
        final String policyId,
        final PromotionDrawRequest request
    ) {
        final PromotionPolicyDocument policy = promotionPolicyMongoRepository.findById(policyId)
            .orElseThrow(ApiException::notFound);
        final PromotionEntryDomain promotionEntry = PromotionEntryDomain.findIn(policy)
            .orElseThrow(ApiException::lotteryNotReady);

        final boolean alreadyDrawn = promotionWinnerMongoRepository.existsByPolicyId(policyId);
        switch (promotionEntry.manualDrawability(alreadyDrawn)) {
            case ALREADY_DRAWN -> throw ApiException.alreadyDrawnPromotion();
            case LOTTERY_TYPE_MISMATCH -> throw ApiException.lotteryNotReady();
            case DRAWABLE -> {
            }
        }

        if (CollectionUtils.isEmpty(request.userIds())) {
            throw ApiException.lotteryNotReady();
        }

        final Set<String> applicantUserIds = new HashSet<>();
        for (final PromotionApplierDocument applier : promotionApplierMongoRepository.findByPolicyId(policyId)) {
            applicantUserIds.add(applier.getUserId());
        }

        final List<PromotionWinnerDocument> winners = new ArrayList<>();
        for (final String userId : request.userIds()) {
            if (!applicantUserIds.contains(userId)) {
                throw ApiException.conditionNotSatisfied();
            }
            if (promotionWinnerMongoRepository.findByPolicyIdAndUserId(policyId, userId).isPresent()) {
                throw ApiException.alreadyDrawnPromotion();
            }
            winners.add(
                promotionWinnerMongoRepository.save(
                    PromotionWinnerDocument.draw(
                        policyId,
                        policy.getKey(),
                        userId,
                        PromotionLotteryType.MANUAL,
                        promotionEntry.prizes(),
                        adminId
                    )
                )
            );
        }

        return PromotionWinnerListResponse.of(winners);
    }
}

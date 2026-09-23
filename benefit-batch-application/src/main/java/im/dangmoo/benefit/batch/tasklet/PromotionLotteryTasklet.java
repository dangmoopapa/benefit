package im.dangmoo.benefit.batch.tasklet;

import im.dangmoo.benefit.batch.parameter.PromotionLotteryParameter;
import im.dangmoo.benefit.domain.promotion.PromotionEntryDomain;
import im.dangmoo.benefit.domain.promotion.PromotionPolicyDomain;
import im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplierDocument;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplierMongoRepository;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.promotion.winner.PromotionWinnerDocument;
import im.dangmoo.benefit.infrastructure.data.promotion.winner.PromotionWinnerMongoRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@StepScope
public class PromotionLotteryTasklet implements Tasklet, StepExecutionListener {

    private final PromotionPolicyMongoRepository promotionPolicyMongoRepository;
    private final PromotionApplierMongoRepository promotionApplierMongoRepository;
    private final PromotionWinnerMongoRepository promotionWinnerMongoRepository;
    private PromotionLotteryParameter parameter;

    public PromotionLotteryTasklet(
        final PromotionPolicyMongoRepository promotionPolicyMongoRepository,
        final PromotionApplierMongoRepository promotionApplierMongoRepository,
        final PromotionWinnerMongoRepository promotionWinnerMongoRepository
    ) {
        this.promotionPolicyMongoRepository = promotionPolicyMongoRepository;
        this.promotionApplierMongoRepository = promotionApplierMongoRepository;
        this.promotionWinnerMongoRepository = promotionWinnerMongoRepository;
    }

    @Override
    public void beforeStep(final @NonNull StepExecution stepExecution) {
        this.parameter = PromotionLotteryParameter.of(stepExecution.getJobParameters());
    }

    @Override
    public RepeatStatus execute(
        final @NonNull StepContribution contribution,
        final @NonNull ChunkContext chunkContext
    ) {
        final List<PromotionPolicyDocument> targets = resolveTargets();
        for (final PromotionPolicyDocument policy : targets) {
            if (draw(policy)) {
                contribution.incrementWriteCount(1);
            }
        }
        return RepeatStatus.FINISHED;
    }

    private List<PromotionPolicyDocument> resolveTargets() {
        if (StringUtils.hasText(parameter.policyKey())) {
            return promotionPolicyMongoRepository.findByKey(parameter.policyKey())
                .map(List::of)
                .orElse(List.of());
        }
        return promotionPolicyMongoRepository.findEndedForAutoLottery(parameter.asOf());
    }

    private boolean draw(final PromotionPolicyDocument policy) {
        final PromotionEntryDomain promotionEntry = PromotionEntryDomain.findIn(policy).orElse(null);
        if (promotionEntry == null) {
            return false;
        }
        if (!PromotionPolicyDomain.of(policy).isEndedAt(parameter.asOf())) {
            return false;
        }

        final boolean alreadyDrawn = promotionWinnerMongoRepository.existsByPolicyId(policy.getId());
        if (promotionEntry.autoDrawability(alreadyDrawn) != PromotionEntryDomain.Drawability.DRAWABLE) {
            return false;
        }

        final List<String> candidateUserIds = promotionApplierMongoRepository.findByPolicyId(policy.getId())
            .stream()
            .map(PromotionApplierDocument::getUserId)
            .toList();
        final Set<String> alreadyWonUserIds = new HashSet<>(
            promotionWinnerMongoRepository.findByPolicyId(policy.getId()).stream()
                .map(PromotionWinnerDocument::getUserId)
                .toList()
        );
        final List<String> winnerUserIds = promotionEntry.drawWinnersFrom(candidateUserIds, alreadyWonUserIds);
        if (winnerUserIds.isEmpty()) {
            return false;
        }

        winnerUserIds.forEach(userId -> promotionWinnerMongoRepository.save(
            PromotionWinnerDocument.draw(
                policy.getId(),
                policy.getKey(),
                userId,
                PromotionLotteryType.AUTO_COUNT,
                promotionEntry.prizes(),
                parameter.requestedBy()
            )
        ));

        if (policy.getStatus() != PromotionPolicyStatus.ENDED) {
            promotionPolicyMongoRepository.save(
                policy.changeStatus(PromotionPolicyStatus.ENDED, parameter.requestedBy())
            );
        }
        return true;
    }
}

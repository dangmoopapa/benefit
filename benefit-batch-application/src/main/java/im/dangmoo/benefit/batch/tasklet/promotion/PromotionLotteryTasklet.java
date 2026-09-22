package im.dangmoo.benefit.batch.tasklet.promotion;

import im.dangmoo.benefit.batch.parameter.promotion.PromotionLotteryParameter;
import im.dangmoo.benefit.domain.promotion.PromotionEntryDomain;
import im.dangmoo.benefit.domain.promotion.PromotionPolicyDomain;
import im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplier;
import im.dangmoo.benefit.infrastructure.data.promotion.applier.PromotionApplierMongoRepository;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.promotion.winner.PromotionWinner;
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
        final List<PromotionPolicy> targets = resolveTargets();
        for (final PromotionPolicy policy : targets) {
            if (draw(policy)) {
                contribution.incrementWriteCount(1);
            }
        }
        return RepeatStatus.FINISHED;
    }

    private List<PromotionPolicy> resolveTargets() {
        if (StringUtils.hasText(parameter.policyKey())) {
            return promotionPolicyMongoRepository.findByKey(parameter.policyKey())
                .map(List::of)
                .orElse(List.of());
        }
        return promotionPolicyMongoRepository.findEndedForAutoLottery(parameter.asOf());
    }

    private boolean draw(final PromotionPolicy policy) {
        final PromotionPolicyDomain policyDomain = PromotionPolicyDomain.of(policy);
        final PromotionEntryDomain entry = policyDomain.entry().orElse(null);
        if (entry == null) {
            return false;
        }

        final boolean alreadyDrawn = promotionWinnerMongoRepository.existsByPolicyId(policy.getId());
        try {
            policyDomain.requireAutoLotteryReady(parameter.asOf(), alreadyDrawn);
        } catch (final PromotionPolicyDomain.LotteryNotReadyException
                       | PromotionEntryDomain.LotteryNotReadyException
                       | PromotionEntryDomain.AlreadyDrawnException ex) {
            return false;
        }

        final List<String> candidateUserIds = promotionApplierMongoRepository.findByPolicyId(policy.getId())
            .stream()
            .map(PromotionApplier::getUserId)
            .toList();
        final Set<String> alreadyWon = new HashSet<>(
            promotionWinnerMongoRepository.findByPolicyId(policy.getId()).stream()
                .map(PromotionWinner::getUserId)
                .toList()
        );
        final List<String> selected = entry.selectWinners(candidateUserIds, alreadyWon);
        if (selected.isEmpty()) {
            return false;
        }

        selected.forEach(userId -> promotionWinnerMongoRepository.save(
            PromotionWinner.draw(
                policy.getId(),
                policy.getKey(),
                userId,
                PromotionLotteryType.AUTO_COUNT,
                entry.prizes(),
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

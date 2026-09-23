package im.dangmoo.benefit.batch.tasklet;

import im.dangmoo.benefit.batch.parameter.PointExpireParameter;
import im.dangmoo.benefit.domain.point.PointBalanceDomain;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceDocument;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionDocument;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionMongoRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@StepScope
public class PointExpireTasklet implements Tasklet, StepExecutionListener {

    private final PointBalanceMongoRepository pointBalanceMongoRepository;
    private final PointTransactionMongoRepository pointTransactionMongoRepository;
    private PointExpireParameter parameter;

    public PointExpireTasklet(
        final PointBalanceMongoRepository pointBalanceMongoRepository,
        final PointTransactionMongoRepository pointTransactionMongoRepository
    ) {
        this.pointBalanceMongoRepository = pointBalanceMongoRepository;
        this.pointTransactionMongoRepository = pointTransactionMongoRepository;
    }

    @Override
    public void beforeStep(final @NonNull StepExecution stepExecution) {
        this.parameter = PointExpireParameter.of(stepExecution.getJobParameters());
    }

    @Override
    public RepeatStatus execute(
        final @NonNull StepContribution contribution,
        final @NonNull ChunkContext chunkContext
    ) {
        while (true) {
            final List<PointBalanceDocument> due = pointBalanceMongoRepository.findDueForExpire(parameter.asOf(), 200);
            if (due.isEmpty()) {
                return RepeatStatus.FINISHED;
            }
            for (final PointBalanceDocument balance : due) {
                expire(balance);
                contribution.incrementWriteCount(1);
            }
        }
    }

    private void expire(final PointBalanceDocument balance) {
        final Instant asOf = parameter.asOf();
        final Map<Instant, Long> expiredAmounts = PointBalanceDomain.of(balance)
            .expiredAmountsByExpiresAt(asOf);
        for (final Map.Entry<Instant, Long> expired : expiredAmounts.entrySet()) {
            pointTransactionMongoRepository.append(
                PointTransactionDocument.expire(
                    balance.getUserId(),
                    expired.getValue(),
                    expired.getKey(),
                    parameter.requestedBy()
                )
            );
        }
        for (int attempt = 0; true; attempt++) {
            final PointBalanceDocument current = pointBalanceMongoRepository.findByUserId(balance.getUserId())
                .orElse(balance);
            try {
                pointBalanceMongoRepository.save(current.syncExpired(asOf));
                return;
            } catch (final org.springframework.dao.OptimisticLockingFailureException ex) {
                if (attempt == 2) {
                    throw ex;
                }
            }
        }
    }
}

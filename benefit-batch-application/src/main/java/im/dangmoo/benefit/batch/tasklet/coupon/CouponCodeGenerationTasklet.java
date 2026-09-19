package im.dangmoo.benefit.batch.tasklet.coupon;

import im.dangmoo.benefit.batch.parameter.coupon.CouponCodeGenerationParameter;
import im.dangmoo.benefit.domain.coupon.CouponRandomCodeDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCode;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.code.CouponCodeType;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class CouponCodeGenerationTasklet implements Tasklet, StepExecutionListener {

    private static final int INSERT_RETRY = 5;

    private final CouponPolicyMongoRepository couponPolicyMongoRepository;
    private final CouponCodeMongoRepository couponCodeMongoRepository;
    private CouponCodeGenerationParameter parameter;

    public CouponCodeGenerationTasklet(
        final CouponPolicyMongoRepository couponPolicyMongoRepository,
        final CouponCodeMongoRepository couponCodeMongoRepository
    ) {
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
        this.couponCodeMongoRepository = couponCodeMongoRepository;
    }

    @Override
    public void beforeStep(final @NonNull StepExecution stepExecution) {
        this.parameter = CouponCodeGenerationParameter.of(stepExecution.getJobParameters());
    }

    @Override
    public RepeatStatus execute(
        final @NonNull StepContribution contribution,
        final @NonNull ChunkContext chunkContext
    ) {
        final CouponPolicy policy = couponPolicyMongoRepository.findByKey(parameter.policyKey())
            .orElseThrow(() -> new IllegalStateException("policy not found: " + parameter.policyKey()));
        final Long stockQuantity = policy.getIssueCondition().getStockQuantity();
        if (stockQuantity == null || stockQuantity <= 0) {
            throw new IllegalStateException("invalid stockQuantity for policy: " + parameter.policyKey());
        }

        final long existing = couponCodeMongoRepository.countByPolicyId(policy.getId());
        final long remaining = stockQuantity - existing;
        if (remaining <= 0) {
            return RepeatStatus.FINISHED;
        }

        for (int i = 0; i < remaining; i++) {
            insertRandomCode(policy);
        }
        return RepeatStatus.FINISHED;
    }

    private void insertRandomCode(final CouponPolicy policy) {
        String candidate = CouponRandomCodeDomain.generate();
        for (int attempt = 0; attempt < INSERT_RETRY; attempt++) {
            final var inserted = couponCodeMongoRepository.insertIgnoreDuplicate(
                CouponCode.create(
                    policy.getId(),
                    policy.getKey(),
                    candidate,
                    CouponCodeType.RANDOM,
                    parameter.requestedBy()
                )
            );
            if (inserted.isPresent()) {
                return;
            }
            candidate = CouponRandomCodeDomain.generate();
        }
        throw new IllegalStateException("could not generate unique coupon code");
    }
}

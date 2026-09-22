package im.dangmoo.benefit.batch.job.coupon;

import im.dangmoo.benefit.batch.job.JobName;
import im.dangmoo.benefit.batch.tasklet.coupon.CouponCodeGenerationTasklet;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CouponCodeGenerationJobConfig {

    private static final String STEP = "COUPON_CODE_GENERATION_STEP";

    @Bean
    Job couponCodeGenerationJob(
        final JobRepository jobRepository,
        final Step couponCodeGenerationStep
    ) {
        return new JobBuilder(JobName.COUPON_CODE_GENERATION.name(), jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(couponCodeGenerationStep)
            .build();
    }

    @Bean
    Step couponCodeGenerationStep(
        final JobRepository jobRepository,
        final PlatformTransactionManager transactionManager,
        final CouponCodeGenerationTasklet couponCodeGenerationTasklet
    ) {
        return new StepBuilder(STEP, jobRepository)
            .tasklet(couponCodeGenerationTasklet, transactionManager)
            .listener(couponCodeGenerationTasklet)
            .build();
    }
}

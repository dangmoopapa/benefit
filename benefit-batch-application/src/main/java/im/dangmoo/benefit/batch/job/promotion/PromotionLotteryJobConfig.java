package im.dangmoo.benefit.batch.job.promotion;

import im.dangmoo.benefit.batch.job.JobName;
import im.dangmoo.benefit.batch.tasklet.promotion.PromotionLotteryTasklet;
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
public class PromotionLotteryJobConfig {

    private static final String STEP = "PROMOTION_LOTTERY_STEP";

    @Bean
    Job promotionLotteryJob(
        final JobRepository jobRepository,
        final Step promotionLotteryStep
    ) {
        return new JobBuilder(JobName.PROMOTION_LOTTERY.name(), jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(promotionLotteryStep)
            .build();
    }

    @Bean
    Step promotionLotteryStep(
        final JobRepository jobRepository,
        final PlatformTransactionManager transactionManager,
        final PromotionLotteryTasklet promotionLotteryTasklet
    ) {
        return new StepBuilder(STEP, jobRepository)
            .tasklet(promotionLotteryTasklet, transactionManager)
            .listener(promotionLotteryTasklet)
            .build();
    }
}

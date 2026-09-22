package im.dangmoo.benefit.batch.job.point;

import im.dangmoo.benefit.batch.job.JobName;
import im.dangmoo.benefit.batch.tasklet.point.PointExpireTasklet;
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
public class PointExpireJobConfig {

    private static final String STEP = "POINT_EXPIRE_STEP";

    @Bean
    Job pointExpireJob(
        final JobRepository jobRepository,
        final Step pointExpireStep
    ) {
        return new JobBuilder(JobName.POINT_EXPIRE.name(), jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(pointExpireStep)
            .build();
    }

    @Bean
    Step pointExpireStep(
        final JobRepository jobRepository,
        final PlatformTransactionManager transactionManager,
        final PointExpireTasklet pointExpireTasklet
    ) {
        return new StepBuilder(STEP, jobRepository)
            .tasklet(pointExpireTasklet, transactionManager)
            .listener(pointExpireTasklet)
            .build();
    }
}

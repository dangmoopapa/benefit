package im.dangmoo.benefit.batch.support;

import java.util.Collection;
import java.util.Properties;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConditionalOnProperty(name = "spring.batch.job.name")
public class BatchJobLauncher implements ApplicationRunner, ExitCodeGenerator {

    private static final String TIMESTAMP = "timestamp";

    private final JobOperator jobOperator;
    private final Collection<Job> jobs;
    private final Environment environment;
    private final JobParametersConverter converter = new DefaultJobParametersConverter();
    private int exitCode = 0;

    public BatchJobLauncher(
        final JobOperator jobOperator,
        final Collection<Job> jobs,
        final Environment environment
    ) {
        this.jobOperator = jobOperator;
        this.jobs = jobs;
        this.environment = environment;
    }

    @Override
    public void run(final ApplicationArguments args) throws Exception {
        final String jobName = environment.getProperty("spring.batch.job.name");
        if (!StringUtils.hasText(jobName)) {
            throw new IllegalStateException("spring.batch.job.name is required");
        }

        final Job job = jobs.stream()
            .filter(candidate -> candidate.getName().equals(jobName))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No job found with name '" + jobName + "'"));

        final Properties properties = StringUtils.splitArrayElementsIntoProperties(
            args.getNonOptionArgs().toArray(String[]::new),
            "="
        );
        final Properties jobProperties = properties != null ? properties : new Properties();
        if (!jobProperties.containsKey(TIMESTAMP)) {
            jobProperties.setProperty(TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        }

        final JobParameters jobParameters = converter.getJobParameters(jobProperties);
        final JobExecution execution = jobOperator.start(job, jobParameters);
        if (execution.getStatus().isUnsuccessful()) {
            exitCode = 1;
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}

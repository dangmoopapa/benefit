package im.dangmoo.benefit.batch.parameter;

import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.util.StringUtils;

import java.time.Instant;

public record PointExpireParameter(
    Instant asOf,
    String requestedBy
) {

    public static final String AS_OF = "asOf";
    public static final String REQUESTED_BY = "requestedBy";

    public static PointExpireParameter of(final JobParameters jobParameters) {
        final String asOfValue = jobParameters.getString(AS_OF);
        final Instant asOf = StringUtils.hasText(asOfValue) ? Instant.parse(asOfValue) : Instant.now();
        final String requestedBy = jobParameters.getString(REQUESTED_BY);
        return new PointExpireParameter(
            asOf,
            StringUtils.hasText(requestedBy) ? requestedBy : "batch"
        );
    }
}

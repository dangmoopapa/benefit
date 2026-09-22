package im.dangmoo.benefit.batch.parameter.promotion;

import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.util.StringUtils;

import java.time.Instant;

public record PromotionLotteryParameter(
    Instant asOf,
    String requestedBy,
    String policyKey
) {

    public static final String AS_OF = "asOf";
    public static final String REQUESTED_BY = "requestedBy";
    public static final String POLICY_KEY = "policyKey";

    public static PromotionLotteryParameter of(final JobParameters jobParameters) {
        final String asOfValue = jobParameters.getString(AS_OF);
        final Instant asOf = StringUtils.hasText(asOfValue) ? Instant.parse(asOfValue) : Instant.now();
        final String requestedBy = jobParameters.getString(REQUESTED_BY);
        final String policyKey = jobParameters.getString(POLICY_KEY);
        return new PromotionLotteryParameter(
            asOf,
            StringUtils.hasText(requestedBy) ? requestedBy : "batch",
            StringUtils.hasText(policyKey) ? policyKey : null
        );
    }
}

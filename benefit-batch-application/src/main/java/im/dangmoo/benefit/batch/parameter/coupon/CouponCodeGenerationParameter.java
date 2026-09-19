package im.dangmoo.benefit.batch.parameter.coupon;

import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.util.StringUtils;

public record CouponCodeGenerationParameter(
    String policyKey,
    String requestedBy
) {

    public static final String POLICY_KEY = "policyKey";
    public static final String REQUESTED_BY = "requestedBy";

    public static CouponCodeGenerationParameter of(final JobParameters jobParameters) {
        final String policyKey = jobParameters.getString(POLICY_KEY);
        if (!StringUtils.hasText(policyKey)) {
            throw new IllegalArgumentException("policyKey is required");
        }
        final String requestedBy = jobParameters.getString(REQUESTED_BY);
        return new CouponCodeGenerationParameter(
            policyKey,
            StringUtils.hasText(requestedBy) ? requestedBy : "batch"
        );
    }
}

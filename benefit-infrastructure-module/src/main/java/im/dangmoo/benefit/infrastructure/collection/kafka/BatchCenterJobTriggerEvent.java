package im.dangmoo.benefit.infrastructure.collection.kafka;

import java.time.Instant;
import java.util.Map;

public record BatchCenterJobTriggerEvent(
    String jobName,
    Map<String, String> parameters,
    Instant occurredAt
) {

    public static final String COUPON_CODE_GENERATION = "COUPON_CODE_GENERATION";

    public static BatchCenterJobTriggerEvent of(
        final String jobName,
        final Map<String, String> parameters
    ) {
        return new BatchCenterJobTriggerEvent(jobName, Map.copyOf(parameters), Instant.now());
    }
}

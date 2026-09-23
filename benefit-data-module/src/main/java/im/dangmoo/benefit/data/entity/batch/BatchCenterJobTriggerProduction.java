package im.dangmoo.benefit.data.entity.batch;

import java.time.Instant;
import java.util.Map;

public record BatchCenterJobTriggerProduction(
    String jobName,
    Map<String, String> parameters,
    Instant occurredAt
) {
    public static BatchCenterJobTriggerProduction of(
        final String jobName,
        final Map<String, String> parameters
    ) {
        return new BatchCenterJobTriggerProduction(jobName, Map.copyOf(parameters), Instant.now());
    }
}

package im.dangmoo.benefit.infrastructure.data.batch;

import java.time.Instant;
import java.util.Map;

public record BatchCenterJobTriggerEvent(
    String jobName,
    Map<String, String> parameters,
    Instant occurredAt
) {
    public static BatchCenterJobTriggerEvent of(
        final String jobName,
        final Map<String, String> parameters
    ) {
        return new BatchCenterJobTriggerEvent(jobName, Map.copyOf(parameters), Instant.now());
    }
}

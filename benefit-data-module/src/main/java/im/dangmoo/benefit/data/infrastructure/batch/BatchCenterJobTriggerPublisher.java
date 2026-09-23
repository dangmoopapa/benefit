package im.dangmoo.benefit.data.infrastructure.batch;

import im.dangmoo.benefit.data.infrastructure.KafkaProducerTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import im.dangmoo.benefit.data.entity.batch.*;

@Component
public class BatchCenterJobTriggerPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BatchCenterJobTriggerPublisher(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final BatchCenterJobTriggerProduction event) {
        kafkaTemplate.send(KafkaProducerTopics.BATCH_CENTER_JOB_TRIGGER, event.jobName(), event);
    }
}

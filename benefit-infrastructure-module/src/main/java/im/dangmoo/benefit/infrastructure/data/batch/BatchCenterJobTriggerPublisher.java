package im.dangmoo.benefit.infrastructure.data.batch;

import im.dangmoo.benefit.infrastructure.collection.kafka.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BatchCenterJobTriggerPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BatchCenterJobTriggerPublisher(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final BatchCenterJobTriggerEvent event) {
        kafkaTemplate.send(KafkaTopics.BATCH_CENTER_JOB_TRIGGER, event.jobName(), event);
    }
}

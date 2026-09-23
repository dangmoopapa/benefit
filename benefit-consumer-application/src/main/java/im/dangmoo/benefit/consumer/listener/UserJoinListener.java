package im.dangmoo.benefit.consumer.listener;

import im.dangmoo.benefit.consumer.KafkaConsumerTopics;
import im.dangmoo.benefit.consumer.consumption.UserJoinConsumption;
import im.dangmoo.benefit.consumer.handler.UserJoinHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserJoinListener {

    private final UserJoinHandler userJoinHandler;

    public UserJoinListener(final UserJoinHandler userJoinHandler) {
        this.userJoinHandler = userJoinHandler;
    }

    @KafkaListener(topics = KafkaConsumerTopics.USER_JOIN)
    public void listen(final UserJoinConsumption consumption) {
        if (consumption == null || !StringUtils.hasText(consumption.userId())) {
            return;
        }
        userJoinHandler.handle(consumption);
    }
}

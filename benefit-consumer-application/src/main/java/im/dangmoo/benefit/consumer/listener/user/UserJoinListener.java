package im.dangmoo.benefit.consumer.listener.user;

import im.dangmoo.benefit.consumer.KafkaConsumerTopics;
import im.dangmoo.benefit.consumer.event.user.UserJoinEvent;
import im.dangmoo.benefit.consumer.handler.user.UserJoinHandler;
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
    public void listen(final UserJoinEvent event) {
        if (event == null || !StringUtils.hasText(event.userId())) {
            return;
        }
        userJoinHandler.handle(event);
    }
}

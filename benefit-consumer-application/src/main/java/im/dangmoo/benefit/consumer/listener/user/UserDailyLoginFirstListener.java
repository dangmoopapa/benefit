package im.dangmoo.benefit.consumer.listener.user;

import im.dangmoo.benefit.consumer.KafkaConsumerTopics;
import im.dangmoo.benefit.consumer.event.user.UserDailyLoginFirstEvent;
import im.dangmoo.benefit.consumer.handler.user.UserDailyLoginFirstHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserDailyLoginFirstListener {

    private final UserDailyLoginFirstHandler userDailyLoginFirstHandler;

    public UserDailyLoginFirstListener(final UserDailyLoginFirstHandler userDailyLoginFirstHandler) {
        this.userDailyLoginFirstHandler = userDailyLoginFirstHandler;
    }

    @KafkaListener(topics = KafkaConsumerTopics.USER_DAILY_LOGIN_FIRST)
    public void listen(final UserDailyLoginFirstEvent event) {
        if (event == null || !StringUtils.hasText(event.userId())) {
            return;
        }
        userDailyLoginFirstHandler.handle(event);
    }
}

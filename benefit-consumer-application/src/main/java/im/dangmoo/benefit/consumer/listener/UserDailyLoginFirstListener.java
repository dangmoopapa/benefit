package im.dangmoo.benefit.consumer.listener;

import im.dangmoo.benefit.consumer.KafkaConsumerTopics;
import im.dangmoo.benefit.consumer.consumption.UserDailyLoginFirstConsumption;
import im.dangmoo.benefit.consumer.handler.UserDailyLoginFirstHandler;
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
    public void listen(final UserDailyLoginFirstConsumption consumption) {
        if (consumption == null || !StringUtils.hasText(consumption.userId())) {
            return;
        }
        userDailyLoginFirstHandler.handle(consumption);
    }
}

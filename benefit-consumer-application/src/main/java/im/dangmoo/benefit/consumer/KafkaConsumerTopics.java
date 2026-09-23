package im.dangmoo.benefit.consumer;

import im.dangmoo.benefit.infrastructure.collection.kafka.KafkaTopics;

public final class KafkaConsumerTopics {

    public static final String USER_DAILY_LOGIN_FIRST = KafkaTopics.USER_DAILY_LOGIN_FIRST;
    public static final String USER_JOIN = KafkaTopics.USER_JOIN;
    public static final String COUPON_TIME_ATTACK_WALLET_ISSUE = KafkaTopics.COUPON_TIME_ATTACK_WALLET_ISSUE;

    private KafkaConsumerTopics() {
    }
}

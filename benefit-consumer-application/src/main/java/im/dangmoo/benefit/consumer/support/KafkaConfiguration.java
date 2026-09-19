package im.dangmoo.benefit.consumer.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJacksonJsonMessageConverter;

@Configuration
public class KafkaConfiguration {

    @Bean
    RecordMessageConverter recordMessageConverter() {
        return new StringJacksonJsonMessageConverter();
    }
}

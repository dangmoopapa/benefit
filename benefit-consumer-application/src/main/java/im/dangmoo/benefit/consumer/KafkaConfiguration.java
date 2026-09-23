package im.dangmoo.benefit.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJacksonJsonMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJacksonJavaTypeMapper;
import org.springframework.kafka.support.mapping.JacksonJavaTypeMapper;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class KafkaConfiguration {

    @Bean
    RecordMessageConverter recordMessageConverter(final JsonMapper jsonMapper) {
        final var typeMapper = new DefaultJacksonJavaTypeMapper();
        typeMapper.setTypePrecedence(JacksonJavaTypeMapper.TypePrecedence.INFERRED);

        final var converter = new StringJacksonJsonMessageConverter(jsonMapper);
        converter.setTypeMapper(typeMapper);
        return converter;
    }
}

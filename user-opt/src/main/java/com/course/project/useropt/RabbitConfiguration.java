package com.course.project.useropt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableRabbit
public class RabbitConfiguration {

    public static final String QUEUE_NAME_USER_REGISTER = "user_register_queue";
    public static final String EXCHANGE_NAME_USER_REGISTER = "user_register_exchange";
    public static final String ROUTING_KEY_USER_REGISTER = "user_register_routing";

    public static final String QUEUE_NAME_USER_OPT = "user_opt_queue";
    public static final String EXCHANGE_NAME_USER_OPT = "user_opt_exchange";
    public static final String ROUTING_KEY_USER_OPT = "user_opt_routing";

    // https://stackoverflow.com/a/59970323
    @Bean
    public Jackson2JsonMessageConverter converter(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}

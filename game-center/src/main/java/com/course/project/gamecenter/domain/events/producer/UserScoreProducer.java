package com.course.project.gamecenter.domain.events.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserScoreProducer {

    //todo
    public static final String EXCHANGE_NAME = "user_score_exchange";
    public static final String ROUTING_NAME = "user_score_routing";

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public UserScoreProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sender(Object msg) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_NAME, msg);
    }
}

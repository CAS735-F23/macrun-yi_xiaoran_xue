package com.course.project.macrunyi_xiaoran_xue.domain.event.producer;

import com.course.project.macrunyi_xiaoran_xue.cfg.CfgEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserRegisterProducer {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public UserRegisterProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sender(Object msg) {
        rabbitTemplate.convertAndSend(CfgEnum.USER_REGISTER.getEXCHANGE_NAME(), CfgEnum.USER_REGISTER.getROUTING_NAME(), msg);
    }
}
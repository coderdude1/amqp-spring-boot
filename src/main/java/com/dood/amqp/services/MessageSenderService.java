package com.dood.amqp.services;

import com.dood.amqp.config.AmqpConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderService {
    private final RabbitTemplate rabbitTemplate;

    public MessageSenderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSimpleQueueMesssage(String simpleMessage) {
        rabbitTemplate.convertAndSend(AmqpConfig.QUEUE_NAME, simpleMessage);
    }
}

package com.dood.amqp.services;

import com.dood.amqp.config.AmqpConfig;
import com.dood.amqp.config.AmqpExceptionsConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderService {
    private final RabbitTemplate rabbitTemplate;

    public MessageSenderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSimpleQueueMesssage(String simpleMessage) {
        rabbitTemplate.convertAndSend(AmqpConfig.SIMPLE_RECEIVER_QUEUE, simpleMessage);
    }

    public void sendMessageAwareMessage(String simpleMessage) {
        rabbitTemplate.convertAndSend(AmqpConfig.MESSAGE_AWARE_RECEIVER_QUEUE, simpleMessage);
    }

    public void sendMessageAwareExceptionMessage(String message) {
        rabbitTemplate.convertAndSend(AmqpExceptionsConfig.MESSAGE_AWARE_THAT_THROWS_EXCEPTION_QUEUE, message);
    }
}

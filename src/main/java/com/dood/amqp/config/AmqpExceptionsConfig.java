package com.dood.amqp.config;

import com.dood.amqp.receivers.MessageAwareThatThrowsException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure receivers for showing off some exception scenarios
 */
@Configuration
public class AmqpExceptionsConfig {

    public static final String MESSAGE_AWARE_THAT_THROWS_EXCEPTION_QUEUE = "MessageAwareThatThrowsException";
    public static final String MESSAGE_AWARE_EXCEPTION_EXCHANGE = "MessageAwareExceptionExchange";

    //step 1 declare the queue
    @Bean
    Queue simpleReceiverExceptionQueue() {
        return new Queue(MESSAGE_AWARE_THAT_THROWS_EXCEPTION_QUEUE, false);
    }

    //step 2 declare the exchange and configure it (if needed)
    @Bean
    TopicExchange messageAwareReceiverExceptionExchange() {
        return new TopicExchange(MESSAGE_AWARE_EXCEPTION_EXCHANGE);
    }

    //step 3 bind the queue and exchange, name it
    @Bean
    Binding bindMessageAwareReceiver(Queue simpleReceiverExceptionQueue,
                                     TopicExchange messageAwareReceiverExceptionExchange) {
        return BindingBuilder.bind(simpleReceiverExceptionQueue).to(messageAwareReceiverExceptionExchange)
                .with(MESSAGE_AWARE_THAT_THROWS_EXCEPTION_QUEUE);
    }

    //step 4 map the receiver class to a MessageListenerAdapter
    @Bean
    MessageListenerAdapter messageAwareThatThrowsExceptionAdapter(MessageAwareThatThrowsException messageAwareThatThrowsException) {
        return new MessageListenerAdapter(messageAwareThatThrowsException, "receiveMessage");
    }

    @Bean
    SimpleMessageListenerContainer messageAwareExceptionContainer(ConnectionFactory connectionFactory,
                                                           MessageListenerAdapter messageAwareThatThrowsExceptionAdapter) {
        return AmqpConfig.getSimpleMessageListenerContainer(connectionFactory, messageAwareThatThrowsExceptionAdapter,
                MESSAGE_AWARE_THAT_THROWS_EXCEPTION_QUEUE);
    }
}

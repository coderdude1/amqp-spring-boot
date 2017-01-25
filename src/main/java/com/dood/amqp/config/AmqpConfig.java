package com.dood.amqp.config;

import com.dood.amqp.receivers.MessageAwareReceiver;
import com.dood.amqp.receivers.SimpleReceiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
    public static final String SIMPLE_RECEIVER_QUEUE = "simple-receiver";
    public static final String SIMPLE_RECEIVER_TOPIC_EXCAHNGE = "simple-receiver-topic-exchange";
    public static final String MESSAGE_AWARE_RECEIVER_QUEUE = "messageAwareReceiverQueue";
    public static final String MESSAGE_AWARE_RECEIVER_EXCHANGE = "message-aware-receiver-exchange";

    @Bean
    Queue simpleReceiverQueue() {
        return new Queue(SIMPLE_RECEIVER_QUEUE, false);
    }

    @Bean
    TopicExchange simpleReceiverExchange() {
        return new TopicExchange(SIMPLE_RECEIVER_TOPIC_EXCAHNGE);
    }

    @Bean
    Queue messageAwareReceiverQueue() {
        return new Queue(MESSAGE_AWARE_RECEIVER_QUEUE, false);
    }

    @Bean
    TopicExchange messageAwareReceiverExchange() {
        return new TopicExchange(MESSAGE_AWARE_RECEIVER_EXCHANGE);
    }

    @Bean
    Binding bindMessageAwareReceiver(Queue messageAwareReceiverQueue,
                                     TopicExchange messageAwareReceiverExchange) {
        return BindingBuilder.bind(messageAwareReceiverQueue).to(messageAwareReceiverExchange)
                .with(MESSAGE_AWARE_RECEIVER_QUEUE);
    }

    @Bean
    Binding bindSimpleReceiver(Queue simpleReceiverQueue, TopicExchange simpleReceiverExchange) {
        return BindingBuilder.bind(simpleReceiverQueue).to(simpleReceiverExchange)
                .with(SIMPLE_RECEIVER_QUEUE);
    }

    //currently don't know if I should create different connection factores, proably
    //TODO refactor this into 2 seperate config classes, one for each queue/exchange/container
    @Bean
    SimpleMessageListenerContainer simpleReceiverContainer(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter simpleReceiverListenerAdapter) {
        return getSimpleMessageListenerContainer(connectionFactory, simpleReceiverListenerAdapter,
                SIMPLE_RECEIVER_QUEUE);
    }

    /**
     * This allows us to map a pojo method as a reciever of a message
     */
    @Bean
    MessageListenerAdapter simpleReceiverListenerAdapter(SimpleReceiver simpleReceiver) {
        return new MessageListenerAdapter(simpleReceiver, "receiveMessage");
    }

    @Bean
    SimpleMessageListenerContainer messageAwareContainer(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter messageAwareListenerAdapater) {
        return getSimpleMessageListenerContainer(connectionFactory, messageAwareListenerAdapater,
                MESSAGE_AWARE_RECEIVER_QUEUE);
    }

    public static SimpleMessageListenerContainer getSimpleMessageListenerContainer(ConnectionFactory connectionFactory,
                                                                             MessageListenerAdapter messageAwareListenerAdapater,
                                                                             String queueName) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(messageAwareListenerAdapater);
//        container.setMessageConverter(jsonMessageConverter());
        return container;
    }

    @Bean MessageListenerAdapter messageAwareListenerAdapater(MessageAwareReceiver messageAwareReceiver) {
        return new MessageListenerAdapter(messageAwareReceiver);
    }

    //If this is uncommented, it will break the autoinjected RabbitTemplate in
    //simpleReceiver.  YOu may need to declare a restTemplate that uses the
    //SImpleConverter and manually inject that into that reveiver
//    @Bean
//    public MessageConverter jsonMessageConverter(){
//        return new Jackson2JsonMessageConverter();
//    }
}

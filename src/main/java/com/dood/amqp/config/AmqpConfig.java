package com.dood.amqp.config;

import com.dood.amqp.receivers.SimpleReceiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
    public static final String SIMPLE_RECEIVER_QUEUE = "simple-receiver";
    public static final String SIMPLE_RECEIVER_TOPIC_EXCAHNGE = "simple-receiver-topic-exchange";
    public static final String MESSAGE_AWARE_RECEIVER_QUEUE = "messageAwareReceiverQueue";

    @Bean
    Queue simpleReceiverQueue() {
        return new Queue(SIMPLE_RECEIVER_QUEUE, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(SIMPLE_RECEIVER_TOPIC_EXCAHNGE);
    }


    @Bean
    Queue messageAwareReceiverQueue() {
        return new Queue(MESSAGE_AWARE_RECEIVER_QUEUE, false);
    }
    @Bean
    Binding bindSimpleReceiver(Queue simpleReceiverQueue, TopicExchange simpleReceiverExchange) {
        return BindingBuilder.bind(simpleReceiverQueue).to(simpleReceiverExchange)
                .with(SIMPLE_RECEIVER_QUEUE);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(SIMPLE_RECEIVER_QUEUE);
        container.setMessageListener(listenerAdapter);
//        container.setMessageConverter(jsonMessageConverter());
        return container;
    }

    /**
     * This allows us to map a pojo method as a reciever of a message
     */
    @Bean
    MessageListenerAdapter listenerAdapter(SimpleReceiver simpleReceiver) {
        return new MessageListenerAdapter(simpleReceiver, "receiveMessage");
    }

    //If this is uncommented, it will break the autoinjected RabbitTemplate in
    //simpleReceiver.  YOu may need to declare a restTemplate that uses the
    //SImpleConverter and manually inject that into that reveiver
//    @Bean
//    public MessageConverter jsonMessageConverter(){
//        return new Jackson2JsonMessageConverter();
//    }
}

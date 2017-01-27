package com.dood.amqp.config;

import com.dood.amqp.receivers.ChannelAwareMessageListenerReceiver;
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
 * Config for the ChannelAwareMessageListener experiments using the AMQP channel instead of pure spring
 */
@Configuration
public class MqChannelReceiverConfig {

    public static final String CHANNEL_RECEIVER_QUEUE = "channelReceiverQueue";
    public static final String CHANNEL_AWARE_EXCHANGE = "channelAwareExchange";

    @Bean
    Queue channelReceiverQueue() {
        return new Queue(CHANNEL_RECEIVER_QUEUE);
    }

    @Bean
    TopicExchange channelAwareExchange() {
        return new TopicExchange(CHANNEL_AWARE_EXCHANGE);
    }

    @Bean
    Binding bindChannelAwareReceiver(Queue channelReceiverQueue,
                                     TopicExchange channelAwareExchange) {
        return BindingBuilder.bind(channelReceiverQueue)
                .to(channelAwareExchange)
                .with(CHANNEL_RECEIVER_QUEUE);
    }

    @Bean
    MessageListenerAdapter channelAwareAdapter(ChannelAwareMessageListenerReceiver channelAwareMessageListenerReceiver) {
        return new MessageListenerAdapter(channelAwareMessageListenerReceiver, "receiveMessage");
    }

    @Bean
    SimpleMessageListenerContainer channelAwareContainer(ConnectionFactory connectionFactory,
                                                         MessageListenerAdapter channelAwareAdapter) {
        return AmqpConfig.getSimpleMessageListenerContainer(connectionFactory, channelAwareAdapter,
                CHANNEL_RECEIVER_QUEUE);
    }
}

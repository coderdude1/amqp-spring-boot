package com.dood.amqp.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Queue;

@Configuration
public class DeadLetterQueuesConfig {

    public static final String QUEUE_WITH_A_DLX = "quueue-with-a-dlx-ttl";
    public static final String QUEUE_WITH_A_DLX_DLX = QUEUE_WITH_A_DLX + "-DLX";

    @Autowired
    private ConnectionFactory cachingConnectionFactory;

    // Setting the annotation listeners to use the jackson2JsonMessageConverter
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
//        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }
    @Bean
    Queue simplePojoReceiverWithDlxTTL() {
        Map<String, Object> args = new HashMap<String, Object>();

        // The default exchange
        args.put("x-dead-letter-exchange", "");
        // Route to the incoming queue when the TTL occurs
        args.put("x-dead-letter-routing-key", QUEUE_WITH_A_DLX_DLX);
        // TTL 5 seconds
        args.put("x-message-ttl", 5000);//try without this for a test
        return new Queue(QUEUE_WITH_A_DLX, false, false, false, args);
    }

    @Bean
    Queue simplePojoReceiverDlx() {
        return new Queue(QUEUE_WITH_A_DLX_DLX);
    }
}

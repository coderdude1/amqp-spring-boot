package com.dood.amqp.config;

import com.dood.amqp.receivers.MessageAwareThatThrowsBarfsToDlx;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DeadLetterQueuesConfig {

    public static final String QUEUE_WITH_A_DLX = "queue-with-a-dlx-ttl";
    public static final String QUEUE_WITH_A_DLX_DLX = QUEUE_WITH_A_DLX + "-DLX";
    public static final String SIMPLE_POJO_PROGRAMMATIC_DLX = "simplePojoProgrammaticDlx";
    public static final String DLX_FOR_SIMPLE_POJO_PROGRAMTTIC_DLX = "dlxForSimpleProgrammaticDlx";
    public static final String PROGRAMATTIC_ERROR_EXCHANGE = "ProgramatticErrorExchange";
    public static final String DLX_QUEUE_FOR_RUNTIME_EXCEPTION = "dlx-queue-for-runtime-exception";
    public static final String X_DEAD_LETTER_EXCHANGE_KEY = "x-dead-letter-exchange";

    @Autowired
    private ConnectionFactory cachingConnectionFactory;

    @Bean
    Queue queueWithDlxTimeToLive() {
        Map<String, Object> args = new HashMap<>();

        // The default exchange
        args.put(X_DEAD_LETTER_EXCHANGE_KEY, "");//using the defalt exchange, requires a routing key
        // Route to the incoming queue when the TTL occurs
        args.put("x-dead-letter-routing-key", QUEUE_WITH_A_DLX_DLX);
        // TTL 5 seconds
        args.put("x-message-ttl", 5000);//try without this for a test
        return new Queue(QUEUE_WITH_A_DLX, false, false, false, args);
    }

    @Bean
    Queue dlxQueueForTtlDlx() {
        return new Queue(QUEUE_WITH_A_DLX_DLX);
    }

    @Bean
    Queue simplePojoProgrammaticDlx() {
        Map<String, Object> args = new HashMap<>();

        // The default exchange
        args.put(X_DEAD_LETTER_EXCHANGE_KEY, "");//using the defalt exchange, requires a routing key
        // Route to the incoming queue when the exception occurs
        args.put("x-dead-letter-routing-key", DLX_FOR_SIMPLE_POJO_PROGRAMTTIC_DLX);//note this matches an existing queuename
        return new Queue(SIMPLE_POJO_PROGRAMMATIC_DLX, false, false, false, args);
    }

    @Bean
    Queue dlxQueueForSimplePojoPrograttticDlx() {
        return new Queue(DLX_FOR_SIMPLE_POJO_PROGRAMTTIC_DLX);
    }

    @Bean
    TopicExchange getProgrammaticFailureExchange() {
        return new TopicExchange(PROGRAMATTIC_ERROR_EXCHANGE);
    }

    //bind the queue to the exchange
    @Bean
    Binding bindSimpleReceiver(Queue simpleReceiverQueue, TopicExchange simpleReceiverExchange) {
        return BindingBuilder.bind(simpleReceiverQueue).to(simpleReceiverExchange)
                .with(SIMPLE_POJO_PROGRAMMATIC_DLX);
    }

    //note the type is the class of the receiver, spring does the rest
    @Bean
    MessageListenerAdapter programaticErrorToDlxAdapter(MessageAwareThatThrowsBarfsToDlx messageAwareThatThrowsBarfsToDlx) {
        return new MessageListenerAdapter(messageAwareThatThrowsBarfsToDlx);
    }

    @Bean
    SimpleMessageListenerContainer simpleReceiverContainer(ConnectionFactory connectionFactory,
                                                           MessageListenerAdapter programaticErrorToDlxAdapter) {
        return AmqpConfig.getSimpleMessageListenerContainer(connectionFactory, programaticErrorToDlxAdapter,
                SIMPLE_POJO_PROGRAMMATIC_DLX);
    }


    /* This is the receiver that always throws an RuntimeException */
    @Bean
    Queue queueReceiverAlwaysThrowsException() {
        Map<String, Object> args = new HashMap<>();

        // The default exchange
        args.put(X_DEAD_LETTER_EXCHANGE_KEY, "");
        // Route to the incoming queue when the TTL occurs
        args.put("x-dead-letter-routing-key", DLX_QUEUE_FOR_RUNTIME_EXCEPTION);
        // TTL 5 seconds
        return new Queue("Queue-that-always-throws-runtime-exception", false, false, false, args);
    }

    @Bean
    Queue dlxQueueForRuntimeExcpeeitonQueue() {
        return new Queue(DLX_QUEUE_FOR_RUNTIME_EXCEPTION);
    }
}

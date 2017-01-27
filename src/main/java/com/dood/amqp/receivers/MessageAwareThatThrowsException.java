package com.dood.amqp.receivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

/**
 * This causes the message to be requeued infinitely, I wonder if this is a configurable behaviour
 */
@Service
public class MessageAwareThatThrowsException implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(MessageAwareThatThrowsException.class);

    @Override
    public void onMessage(Message message) {
        LOG.info("in message");
        throw new RuntimeException("kaboom"); //throw unhandled and see what happens
    }
}

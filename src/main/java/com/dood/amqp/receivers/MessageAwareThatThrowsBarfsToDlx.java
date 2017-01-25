package com.dood.amqp.receivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

@Service//component instead?
public class MessageAwareThatThrowsBarfsToDlx implements MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(MessageAwareThatThrowsBarfsToDlx.class);

    @Override
    public void onMessage(Message message) {
        LOG.info("Received message, before pushing to DLX");
        throw new AmqpRejectAndDontRequeueException("rejected due to exception");
    }
}

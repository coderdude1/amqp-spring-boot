package com.dood.amqp.receivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

/**
 * Shows off a receiver that implements the MessageListeneer interface
 */
@Service//componemnt or service?  prov comp?
public class MessageAwareReceiver implements MessageListener{
    private static final Logger LOG = LoggerFactory.getLogger(MessageAwareReceiver.class);

    @Override
    public void onMessage(Message message) {
        LOG.info("Received a message: {}", message);
    }
}

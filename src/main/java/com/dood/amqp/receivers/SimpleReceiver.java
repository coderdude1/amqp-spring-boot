package com.dood.amqp.receivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleReceiver.class);

    public void receiveMessage(String message) {
        LOG.info("Received <{}>", message);
    }
}

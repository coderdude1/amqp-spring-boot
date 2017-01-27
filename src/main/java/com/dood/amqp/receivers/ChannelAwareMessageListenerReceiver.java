package com.dood.amqp.receivers;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * experimeint with use of the channel object to figure out DLX options, ie can I route to a different dlx based upon
 * conditions.  From what I have read, I don't think I can do that.  I can probably do a RabbitTemplate.sendAndConvert(newDestintaion)
 * based on some loginc determine newDestation
 * <p>
 * Note that spring rabbit hides this in the AmqpRejectAndDontRequeException
 */
@Component
public class ChannelAwareMessageListenerReceiver implements ChannelAwareMessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(ChannelAwareMessageListenerReceiver.class);

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        LOG.info("Received message in the channel aware listener: {}", message);
        String messageText = new String(message.getBody());//I'm sure there are ways to get spring to do this for me
        if ("one".equalsIgnoreCase(messageText)) {
            //route to a non-default DLX using the channel
//            channel.basicNack();
        } else {
            //throw exception to end up in default dlx
            //GetResponse gr = channel.basicGet("jav-md-state-queue", false);//did not work, gr null
            //channel.basicNack(gr.getEnvelope().getDeliveryTag(), false, false);

        }
    }
}

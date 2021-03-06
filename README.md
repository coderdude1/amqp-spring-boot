#AMQP with spring
Experiments with spring boot/amqp/rabbitmq.  I try to use a bunch of different capabilities with RabbitMQ and Spring AQMP

# Basics of rabbitMQ

## Basic workflow
Messages are created by producers, and producers will then push to an exchange.  Note messages may go through more than one exchange, depending upon the message attributes
and how RabbitMQ is configured.  Eventually the message will end up in a queue, whwere a consumer can pick up the message and does stuff.  A consumer can subscribe to one or more queues, or it can poll the queue
at regular intervals to see where messages were delivered since it's last check.


One of the options for a queue is if a message requires acknowledgement.  I think a queue can be configured with this, and i know that a message attribute can specify it.  If ack is true, the consumer will acknowledge that a message has been processed, and it is removed from the queue.  If the consumer dies before the message is processed, eventually another consumer will pick it up as it will not be removed from the queue.  If ack is false, messages can be lost if a consumer dies before processing is comeplete.  The consumer can also perform a NACK (not ack), and based if it is set to redelevered it will be put back in the queue, sent to a dead letter queue if enabled, or lost

#RabbitMQ basics
## Messages
Messages consist of two pieces
  1.  attributes  - metadata about the message, such as content-type, encoding, routing key, persistent, priority, etc.  Used by RabbitMQ for routing and processing
  2.  payload - actual message (object, keys/pointers, etc)

An important attribute is the Routing Key, which is used by most exchanges to determine how to route the message (ie to another exchange, or a queue(s)

### Notes from spring AMQP documentation:


>The 0-8 and 0-9-1 AMQP specifications do not define a Message class or interface. Instead, when performing an operation such as basicPublish(), the content is passed as a byte-array argument and additional properties are passed in as separate arguments. Spring AMQP defines a Message class as part of a more general AMQP domain model representation. The purpose of the Message class is to simply encapsulate the body and properties within a single instance so that the API can in turn be simpler. The Message class definition is quite straightforward.

## Exchanges

The ones currently supported are
  1. direct - route based on exact match with the routing key
  2. topic - pattern match on the routing key to route the message
  3. headers - similar to topic, instead of pattern matching on routing key, uses the message header attributes
  4. fanout - broadcast to all queues that it knows about, regardless of routing key.  Sorta kinda sounds like JMS topics?
  5. default - when we don't specify an exchange, known as the nameless exchange, or "", uses the queue name as the key

## Queues
Stores messages that are consumed by applications.  Shares some properties with exchanges but has some additional props
  1. Name
  2. Duarble - not lost if broker is restarted)
  3. Exclusive - used by one connection only, and deleted when connection is gone
  4. auto-delete - queue is deleted when last consumer is gone
  5. args - some brokers use ti implemnent additional features like TTL (dead letter?)

## Bindings
Rules that exchanges (and other things?) use to route messages to queues.  There can be 0 or more ways to reach a destination

## Receivers
AMQP and spring support multiple ways to declare receivers.  I'll try several different ways of this, such as
  1. Simple POJO that is wired via config as a receiver
  2. Class that implements the MessageListener interface
  3. Class that implements the ChannelAwareMessageListener
  4. RabbitMQ annotations? (@RabbitListener)
  5. ?

## Dead Letter Queues/Exchanges
You can use the following arguments when setting up a new queue to point the dead letter destination.

```
// The default exchange
args.put("x-dead-letter-exchange", "");//using the defalt exchange, requires a routing key
// Route to the incoming queue when the exception occurs
args.put("x-dead-letter-routing-key", DLX_FOR_SIMPLE_POJO_PROGRAMTTIC_DLX);//this should match an existing queuename since we are using the default exchange
```

Look in the DeadLetterQueuesConfig for some examples of setting this up using TTL, and exceptions to route stuff into it's DLQ

https://www.rabbitmq.com/dlx.html


## typical steps
  1. declare a queue
  2. declare an exchange
  3. bind an exchange to a queue
  4. declare an adapter (binds a consumer to an AMQP adapter
  5. create a spring amqp container (assign it a connection factory, assign it a list of queues it cares about, and assign the adpater to it.  this is how a consumer gets mapped to a queue

I'll also experiment with some of the other features such as dead letter queues, etc

# Overview of some of the examples in this repo
## Simple Receiver
if I use the rabbitmq send to a queue option, I need to add property content_type with value text/plain.  The payload is what is converted to a string.  SO far I haven't had luck using a rabbittemplate in a controller to send a string, for some reason it is being configured with a JsonConverter vs simpleConverter.  This turned out to be due to me declaring a JsonConverter in the spring config (it got autowired into the RabbitTemplate as it wasn't declared in the AMQPConfig.  I might change that later

## MessageListener receiver
Got it to work pretty straight forward, not sure how to set the headers/properties
yet on the producer, nor have I got it to work yet from the rabbitMQ management console

## SimpleReceiverWithTTL DLX
this queue doesn't have a receiver, just shows how a mesage can be pushed to the DLX after it's TTL has expired.
The DLX queue doesn't have a receiver either.  Use RabbitMQ management console to push messages on the queue

## MessageAwareThatThrowsException
This receiver tosses an unhandled RuntimeException.  The message will end up getting put back into the queue, which results in it going back into the receiver, infinitly in the example since it always tosses the exception.  I bet this is configurable, and that the defualt for the queue is ACK required, which causes it to resubmit since we never ACK the message.

Things to look at
  1. disable ACK, no DLQ, message lost?
  2. ACK enabled, DLQ, infinite loop?
  3. Integration Testing - https://tamasgyorfi.net/2016/04/21/writing-integration-tests-for-rabbitmq-based-components/
  4. configure retry params.  not sure what happens.  In spring config I found this
```
spring
  rabbitmq:
      listener:
        retry:
          enabled: false
          initial-interval: 2000
          max-attempts: 2
          multiplier: 1.5
          max-interval: 5000
```

Some more DLX stuff to look at
  1.  using a custom exchange vs the default (ie the args.put("x-dead-letter-exchange", REJECTED_EXCHANGE);
  2.  routing to one of several DLX's based upon the failure

# Random stuff and observations
It looks like a container can only contain one consumer class, but can contain multiple queues.  It will send any
message to any of those queues to the mapped receiver.  You need to create a seperate container for each receiver.
Not sure how this is affected yet by fanout exchanges, etc.  I need to split up my config class as the 2 containers
I currently have share a connection factory and this smells wrong.  research

# TODO
1.  more examples of exchanges, and such
2.  retry options
3.  more dead letter options
    * Routing to different DLQ based on failure type
    * options with messages in DLQ
        * retry after some period?
        * persist in db for review
4.  differnent message types, ie pojos vs Message
5.  poison messages (see links)

# Some reading links I found that helped

## Spring AMQP reference
http://docs.spring.io/spring-amqp/reference/html/

##Good overview on RabbitMQ, AQMP and messaging
https://www.compose.com/articles/messaging-amqp-and-rabbitmq-a-speed-guide/
https://www.compose.com/articles/configuring-rabbitmq-exchanges-queues-and-bindings-part-1/
https://www.compose.com/articles/configuring-rabbitmq-exchanges-queues-and-bindings-part-2/

##JSON releated links
http://memorynotfound.com/produce-consume-rabbitmq-spring-json-message-queue/
http://bernhardwenzel.com/blog/2015/04/10/post-spring-boot-rabbitmq/
http://dev.macero.es/2016/10/23/produce-and-consume-json-messages-with-spring-boot-amqp/

##How the various converters work
http://docs.spring.io/spring-amqp/reference/htmlsingle/#_introduction_9

##Dead Letter Queue/Exchange related stuff
###TTL DLX
https://github.com/cl4r1ty/spring-rabbitmq-dead-letter

###Getting a programttic error to end up in the DLX
http://stackoverflow.com/questions/28286334/spring-amqp-nothing-showing-up-in-dead-letter-queue

### Routing keys and Dead Letter Stuff
http://stackoverflow.com/questions/21742232/rabbitmq-dead-letter-exchange-never-getting-messages

### Understanding RabbitMQ Exchange and Queues
http://teddyma.cn/2016/02/22/understanding-rabbitmq-exchange-and-queue/

### RabbitMQ Exchange and Queue Design tradeoff
http://teddyma.cn/2016/02/23/rabbitmq-exchange-design-tradeoffs/

### RabbitMQ HA
https://www.rabbitmq.com/pacemaker.html
http://www.rabbitmq.com/ha.html

### Reliability and RabbitMQ
http://www.rabbitmq.com/reliability.html

### Firehose and tracing messages through the system
http://www.rabbitmq.com/ha.html

## Poison Mesaages
http://kjnilsson.github.io/blog/2014/01/30/spread-the-poison/
http://tafakari.co.ke/2014/07/rabbitmq-poison-messages/


# TODO
1.  send a message with a null routing key, what happens?
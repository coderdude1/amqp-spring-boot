#AMQP with spring
Experiments with spring boot/amqp/rabbitmq

# Receivers
AMQP and spring support multiple ways to declare receivers.  I'll try several
different ways of this, such as
  1. Simple POJO that is wired via config as a receiver
  2. Class that implements the MessageListener interface
  3. Class that implements the ChannelAwareMessageListener
  4. RabbitMQ annotations?
  5. ?

I'll also experiment with some of the other features such as dead letter queues, etc

#random stuff
It looks like a container can only contain one consumer class, but can contain multiple queues.  It will send any
message to any of those queues to the mapped receiver.  You need to create a seperate container for each receiver.
Not sure how this is affected yet by fanout exchanges, etc.  I need to split up my config class as the 2 containers
I currently have share a connection factory and this smells wrong.  research

## Simple Receiver
if I use the rabbitmq send to a queue option, I need to add property
content_type with value text/plain.  The payload is what is converted
to a string.  SO far I haven't had luck using a rabbittemplate
in a controller to send a string, for some reason it is being configured
with a JsonConverter vs simpleConverter.  This turned out to be due to
me declaring a JsonConverter in the spring config (it got autowired into
the RabbitTemplate as it wasn't declared in the AMQPConfig.  I might change
that later

## MessageListener receiver
Got it to work pretty straight forward, not sure how to set the headers/properties
yet on the producer, nor have I got it to work yet from the rabbitMQ management console

## SimpleReceiverWithTTL DLX
this queue doesn't have a receiver, just shows how a mesage can be pushed to the DLX after it's TTL has expired.
The DLX queue doesn't have a receiver either.  Use RabbitMQ management console to push messages on the queue


# Some reading links I found that helped

TTL DLX https://github.com/cl4r1ty/spring-rabbitmq-dead-letter

JSON releated links
http://memorynotfound.com/produce-consume-rabbitmq-spring-json-message-queue/
http://bernhardwenzel.com/blog/2015/04/10/post-spring-boot-rabbitmq/
http://dev.macero.es/2016/10/23/produce-and-consume-json-messages-with-spring-boot-amqp/

How the various converters work
http://docs.spring.io/spring-amqp/reference/htmlsingle/#_introduction_9

Getting a programttic error to end up in the DLX
http://stackoverflow.com/questions/28286334/spring-amqp-nothing-showing-up-in-dead-letter-queue

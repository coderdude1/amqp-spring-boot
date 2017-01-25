#AMQP with spring
Experiments with spring boot/amqp/rabbitmq

# Receivers
AMQP and spring support multiple ways to declare receivers.  I'll try several
different ways of this, such as
  1. Simple POJO that is wired via config as a receiver
  2. Class that implements the MessageListener interface
  3. Class that implements the ChannelAwareMessageListener

I'll also experiment with some of the other features such as dead letter queues, etc

#random stuff
if I use the rabbitmq send to a queue option, I need to add property
content_type with value text/plain.  The payload is what is converted
to a string.  SO far I haven't had luck using a rabbittemplate
in a controller to send a string, for some reason it is being configured
with a JsonConverter vs simpleConverter.  This turned out to be due to
me declaring a JsonConverter in the spring config (it got autowired into
the RabbitTemplate as it wasn't declared in the AMQPConfig.  I might change
that later
#AMQP with spring

#random stuff
if I use the rabbitmq send to a queue option, I need to add property
content_type with value text/plain.  The payload is what is converted
to a string.  SO far I haven't had luck using a rabbittemplate
in a controller to send a string, for some reason it is being configured
with a JsonConverter vs simpleConverter.  This turned out to be due to
me declaring a JsonConverter in the spring config (it got autowired into
the RabbitTemplate as it wasn't declared in the AMQPConfig.  I might change
that later
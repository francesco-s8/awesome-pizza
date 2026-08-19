package it.s8.awesomepizza.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface PizzaQueueService {

    void sendOrder(Long orderId) throws AmqpException;

    void processOrder(Long pizzaOrder, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag);
}

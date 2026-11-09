package it.s8.awesomepizza.service;

import com.rabbitmq.client.Channel;
import it.s8.awesomepizza.dto.event.OrderCreatedEvent;
import java.io.IOException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface PizzaQueueService {

//    void sendOrder(Long orderId) throws AmqpException;

    void processOrder(Long pizzaOrder, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, InterruptedException;

    void onOrderCreated(OrderCreatedEvent event) throws AmqpException;
}

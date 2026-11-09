package it.s8.awesomepizza.service.impl;

import com.rabbitmq.client.Channel;
import it.s8.awesomepizza.dto.event.OrderCreatedEvent;
import it.s8.awesomepizza.service.PizzaOrderService;
import it.s8.awesomepizza.service.PizzaQueueService;
import it.s8.awesomepizza.utils.sleeper.Sleeper;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
public class PizzaQueueServiceImpl implements PizzaQueueService {

  final RabbitTemplate rabbitTemplate;
  final PizzaOrderService pizzaOrderService;
  final Sleeper sleeper;

  PizzaQueueServiceImpl(
      RabbitTemplate rabbitTemplate, PizzaOrderService pizzaOrderService, Sleeper sleeper) {
    this.rabbitTemplate = rabbitTemplate;
    this.pizzaOrderService = pizzaOrderService;
    this.sleeper = sleeper;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @RabbitListener(
      ackMode = "MANUAL",
      queues = "orders",
      containerFactory = "pizzaOrderListenerContainerFactory")
  public void processOrder(
      Long pizzaOrder, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag)
      throws IOException, InterruptedException {

    log.info("Simulating pizza preparation for order ID: {}", pizzaOrder);
    sleeper.sleep(Duration.ofSeconds(8).toMillis());
    pizzaOrderService.prepareOrder(pizzaOrder);
    channel.basicAck(tag, false);
    log.info("Order {} is ready for delivery", pizzaOrder);
  }

  @Override
  @TransactionalEventListener
  public void onOrderCreated(OrderCreatedEvent event) throws AmqpException {
    log.info("Order {} created event received, sending to queue", event.getOrderId());
    rabbitTemplate.convertAndSend("orders", event.getOrderId());
  }
}

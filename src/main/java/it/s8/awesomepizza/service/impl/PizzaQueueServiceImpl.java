package it.s8.awesomepizza.service.impl;

import com.rabbitmq.client.Channel;
import it.s8.awesomepizza.dto.event.OrderCreatedEvent;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.service.PizzaOrderService;
import it.s8.awesomepizza.service.PizzaQueueService;
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

  public PizzaQueueServiceImpl(RabbitTemplate rabbitTemplate, PizzaOrderService pizzaOrderService) {
    this.rabbitTemplate = rabbitTemplate;
    this.pizzaOrderService = pizzaOrderService;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @RabbitListener(ackMode = "MANUAL", queues = "orders")
  public void processOrder(
      Long pizzaOrder, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag)
      throws IOException {

    log.info("Simulating pizza preparation for order ID: {}", pizzaOrder);

    simulatingPizzaMaking();
    pizzaOrderService.prepareOrder(pizzaOrder);
    channel.basicAck(tag, false);

    log.info("Order {} is ready for delivery", pizzaOrder);
  }

  void simulatingPizzaMaking() {
    Thread.ofVirtual()
        .start(() -> {
          try {
            Thread.sleep(Duration.ofSeconds(8).toMillis());
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Order processing thread interrupted for ", e);
            throw new AwesomePizzaException("Order processing thread interrupted", e);
          }
        });
  }

  @TransactionalEventListener
  @Override
  public void onOrderCreated(OrderCreatedEvent event) throws AmqpException {
    log.info("Order {} created event received, sending to queue", event.getOrderId());
    rabbitTemplate.convertAndSend("orders", event.getOrderId());
  }
}

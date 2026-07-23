package it.s8.awesomepizza.service.impl;

import com.rabbitmq.client.Channel;
import it.s8.awesomepizza.event.OrderCreatedEvent;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.repository.PizzaOrderRepository;
import it.s8.awesomepizza.service.PizzaQueueService;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaOrderStatus;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class PizzaQueueServiceImpl implements PizzaQueueService {

  final RabbitTemplate rabbitTemplate;
  final PizzaOrderRepository pizzaOrderRepository;

  public PizzaQueueServiceImpl(
      RabbitTemplate rabbitTemplate, PizzaOrderRepository pizzaOrderRepository) {
    this.rabbitTemplate = rabbitTemplate;
    this.pizzaOrderRepository = pizzaOrderRepository;
  }

  @Override
  @TransactionalEventListener
  public void sendOrder(Long orderId) throws AmqpException {

    rabbitTemplate.convertAndSend("orders", orderId);
    log.info("Order {} sent to queue", orderId);
  }

  @Override
  @RabbitListener(ackMode = "MANUAL", queues = "orders")
  public void processOrder(
      Long pizzaOrder, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag)
      throws InterruptedException, IOException {

    log.info("Order {} will be finished", pizzaOrder);
    try {
      log.info("Simulating pizza preparation for order ID: {}", pizzaOrder);
      Thread.sleep(Duration.ofSeconds(10).toMillis());
      var order =
          pizzaOrderRepository
              .findById(pizzaOrder)
              .orElseThrow(
                  () -> new AwesomePizzaException("Order not found with ID: " + pizzaOrder));
      order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.READY_FOR_DELIVERY.getValue());
      pizzaOrderRepository.save(order);
      channel.basicAck(tag, false);
      log.info("Order {} is ready", pizzaOrder);
    } catch (RuntimeException e) {
      channel.basicNack(tag, false, false);
      log.error("Error processing order {}", pizzaOrder, e);
    }
  }

  @TransactionalEventListener
  public void onOrderCreated(OrderCreatedEvent event) throws AmqpException {
    log.info("Order {} created event received, sending to queue", event.getOrderId());
    sendOrder(event.getOrderId());
  }
}

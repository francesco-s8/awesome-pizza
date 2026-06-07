package it.s8.awesomepizza.service.impl;

import com.rabbitmq.client.Channel;
import it.s8.awesomepizza.enums.OrderStatus;
import it.s8.awesomepizza.exception.PizzaOrderException;
import it.s8.awesomepizza.repository.PizzaOrderRepository;
import it.s8.awesomepizza.service.PizzaQueueService;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

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
  public void sendOrder(Long orderId) throws AmqpException {

    rabbitTemplate.convertAndSend("orders", orderId);
    log.info("Order {} sent to queue", orderId);
  }

  @RabbitListener(ackMode = "MANUAL", queues = "orders", exclusive = true)
  @Override
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
              .orElseThrow(() -> new PizzaOrderException("Order not found with ID: " + pizzaOrder));
      order.setOrderStatus(OrderStatus.READY.name());
      pizzaOrderRepository.save(order);
      channel.basicAck(tag, false);
      log.info("Order {} is ready", pizzaOrder);
    } catch (RuntimeException e) {
      channel.basicNack(tag, false, false);
      log.error("Error processing order {}", pizzaOrder, e);
    }
  }
}

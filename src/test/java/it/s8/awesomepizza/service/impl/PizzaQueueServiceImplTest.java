package it.s8.awesomepizza.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.rabbitmq.client.Channel;
import it.s8.awesomepizza.dto.event.OrderCreatedEvent;
import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.service.PizzaOrderService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.PizzaOrderStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class PizzaQueueServiceImplTest {

  @InjectMocks PizzaQueueServiceImpl pizzaQueueService;
  @Mock RabbitTemplate rabbitTemplate;
  @Mock PizzaOrderService pizzaOrderService;
  @Mock Channel channel;

  @Test
  void sendOrder_sendsMessageAndPersistsOrder_whenValidRequest() {

    var orderEntity =
        PizzaOrder.builder()
            .id(1L)
            .username("ME")
            .orderStatus(PizzaOrderStatus.OrderStatusEnum.IN_PROCESS.getValue())
            .pizzaList(List.of())
            .build();
    pizzaQueueService.onOrderCreated(new OrderCreatedEvent(orderEntity, orderEntity.getId()));

    verify(rabbitTemplate, times(1)).convertAndSend("orders", orderEntity.getId());
  }

  @Test
  void processOrder_invokesPizzaMakingSimulation() throws Exception {
    var service = spy(pizzaQueueService);
    doNothing().when(service).simulatingPizzaMaking();

    service.processOrder(42L, channel, 7L);

    verify(service).simulatingPizzaMaking();
  }

  @Test
  void processOrder_ExceptionWhenPizzaMakingSimulation() {
    var service = spy(pizzaQueueService);
    doThrow(AwesomePizzaException.class).when(service).simulatingPizzaMaking();

    assertThatThrownBy(() -> service.processOrder(42L, channel, 7L))
        .isInstanceOf(AwesomePizzaException.class);
  }

  @Test
  void processOrder_preparesOrderAndAcksMessage() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    var service=spy(pizzaQueueService);
    doAnswer(
            _ -> {
              latch.countDown();
              return null;
            })
        .when(pizzaOrderService)
        .prepareOrder(42L);

    service.processOrder(42L, channel, 7L);

    assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
    assertThat(Thread.currentThread().isAlive()).isTrue();
    verify(pizzaOrderService).prepareOrder(42L);
    verify(channel).basicAck(7L, false);
    verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
  }

  @Test
  void processOrder_whenPreparationFails_nacksMessage() {
    doAnswer(
            _ -> {
              throw new EntityNotFoundException("preparation failed");
            })
        .when(pizzaOrderService)
        .prepareOrder(99L);

    assertThatThrownBy(() -> pizzaQueueService.processOrder(99L, channel, 11L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("preparation failed");
  }
}

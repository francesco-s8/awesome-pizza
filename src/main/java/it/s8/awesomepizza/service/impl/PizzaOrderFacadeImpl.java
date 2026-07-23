package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.dto.OrderDto;
import it.s8.awesomepizza.dto.PizzaOrderDto;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.event.OrderCreatedEvent;
import it.s8.awesomepizza.mapper.PizzaListToPizzaDtoListMapper;
import it.s8.awesomepizza.service.PizzaOrderFacade;
import it.s8.awesomepizza.service.PizzaOrderService;
import it.s8.awesomepizza.service.PizzaQueueService;
import it.s8.awesomepizza.service.PizzaService;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaOrderRequest;
import org.openapitools.model.PizzaOrderStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class PizzaOrderFacadeImpl implements PizzaOrderFacade {

  final PizzaService pizzaService;
  final PizzaQueueService pizzaQueueService;
  final PizzaOrderService pizzaOrderService;
  final PizzaListToPizzaDtoListMapper pizzaListToPIzzaDtoListMapper;
  final ApplicationContext applicationContext;

  public PizzaOrderFacadeImpl(
      PizzaService pizzaService,
      PizzaQueueService pizzaQueueService,
      PizzaOrderService pizzaOrderService,
      PizzaListToPizzaDtoListMapper pizzaListToPIzzaDtoListMapper,
      ApplicationContext applicationContext) {

    this.pizzaService = pizzaService;
    this.pizzaQueueService = pizzaQueueService;
    this.pizzaOrderService = pizzaOrderService;
    this.pizzaListToPIzzaDtoListMapper = pizzaListToPIzzaDtoListMapper;
    this.applicationContext = applicationContext;
  }

  @Override
  @Transactional
  public OrderDto processOrder(PizzaOrderRequest orderRequest) {
    List<Pizza> pizzas = pizzaService.getPizzas(orderRequest.getPizzas());
    new PizzaOrderDto(
        orderRequest.getCustomerName(),
        pizzas,
        PizzaOrderStatus.OrderStatusEnum.IN_PROCESS.getValue());
    var pizzaOrderEntity =
        PizzaOrder.builder()
            .username(orderRequest.getCustomerName())
            .pizzaList(pizzas)
            .orderStatus(PizzaOrderStatus.OrderStatusEnum.IN_PROCESS.getValue())
            .build();
    var entity = pizzaOrderService.saveOrder(pizzaOrderEntity);
    log.info("Order {} saved to database", entity);
    applicationContext.publishEvent(new OrderCreatedEvent(this, entity.getId()));
    return new OrderDto(entity.getId());
  }

  @Override
  public String retrieveOrderStatus(Long orderId) {
    var order = pizzaOrderService.getOrderStatus(orderId);
    // Silly condition to manage already delivered order
    log.info("Order is {}", order);
    if (PizzaOrderStatus.OrderStatusEnum.READY_FOR_DELIVERY
            .getValue()
            .equals(order.getOrderStatus())
        && order.getModifiedAt().plusSeconds(60L).isBefore(Instant.now())) {
      return PizzaOrderStatus.OrderStatusEnum.ALREADY_DELIVERED.getValue();
    }
    return order.getOrderStatus();
  }
}

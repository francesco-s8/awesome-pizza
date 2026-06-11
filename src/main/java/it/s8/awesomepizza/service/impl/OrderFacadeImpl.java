package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.dto.OrderDto;
import it.s8.awesomepizza.dto.OrderRequest;
import it.s8.awesomepizza.dto.PizzaOrderDto;
import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.enums.OrderStatus;
import it.s8.awesomepizza.exception.PizzaOrderAlreadyDeliveredException;
import it.s8.awesomepizza.service.OrderFacade;
import it.s8.awesomepizza.service.PizzaOrderService;
import it.s8.awesomepizza.service.PizzaQueueService;
import it.s8.awesomepizza.service.PizzaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderFacadeImpl implements OrderFacade {

  final PizzaService pizzaService;
  final PizzaQueueService pizzaQueueService;
  final PizzaOrderService pizzaOrderService;

  public OrderFacadeImpl(
      PizzaService pizzaService,
      PizzaQueueService pizzaQueueService,
      PizzaOrderService pizzaOrderService) {

    this.pizzaService = pizzaService;
    this.pizzaQueueService = pizzaQueueService;
    this.pizzaOrderService = pizzaOrderService;
  }

  @Override
  @Transactional
  public OrderDto processOrder(OrderRequest orderRequest) {
    var pizzas = pizzaService.getPizzas(orderRequest.pizzas());
    new PizzaOrderDto(orderRequest.user(), pizzas, OrderStatus.IN_PROCESS.name());
    var pizzaOrderEntity =
        PizzaOrder.builder()
            .username(orderRequest.user())
            .pizzas(pizzas)
            .orderStatus(OrderStatus.IN_PROCESS.name())
            .build();
    var entity = pizzaOrderService.saveOrder(pizzaOrderEntity);
    log.info("Order {} saved to database", entity);
    pizzaQueueService.sendOrder(entity.getId());
    return new OrderDto(entity.getId());
  }

  @Override
  public String retrieveOrderStatus(Long orderId) {
    var order = pizzaOrderService.getOrderStatus(orderId);
    if (OrderStatus.READY.name().equals(order.getOrderStatus())) {
      throw new PizzaOrderAlreadyDeliveredException("Order " + orderId + " is already delivered");
    }
    return order.getOrderStatus();
  }
}

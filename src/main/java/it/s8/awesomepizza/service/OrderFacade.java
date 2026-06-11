package it.s8.awesomepizza.service;

import it.s8.awesomepizza.dto.OrderDto;
import it.s8.awesomepizza.dto.OrderRequest;
import it.s8.awesomepizza.entity.PizzaOrder;

public interface OrderFacade {

  OrderDto processOrder(OrderRequest orderRequest);

  PizzaOrder retrieveOrderStatus(Long orderId);
}

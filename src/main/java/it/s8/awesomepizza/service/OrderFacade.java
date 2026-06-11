package it.s8.awesomepizza.service;

import it.s8.awesomepizza.dto.OrderDto;
import it.s8.awesomepizza.dto.OrderRequest;

public interface OrderFacade {

  OrderDto processOrder(OrderRequest orderRequest);

  String retrieveOrderStatus(Long orderId);
}

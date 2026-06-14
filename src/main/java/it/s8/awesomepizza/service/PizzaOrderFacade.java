package it.s8.awesomepizza.service;

import it.s8.awesomepizza.dto.OrderDto;
import it.s8.awesomepizza.dto.OrderRequest;
import it.s8.awesomepizza.exception.PizzaOrderAlreadyDeliveredException;
import jakarta.persistence.EntityNotFoundException;

public interface PizzaOrderFacade {

  OrderDto processOrder(OrderRequest orderRequest);

  String retrieveOrderStatus(Long orderId)
      throws EntityNotFoundException, PizzaOrderAlreadyDeliveredException;
}

package it.s8.awesomepizza.service;

import it.s8.awesomepizza.dto.OrderDto;
import jakarta.persistence.EntityNotFoundException;
import org.openapitools.model.PizzaOrderRequest;

public interface PizzaOrderFacade {

  OrderDto processOrder(PizzaOrderRequest orderRequest);

  String retrieveOrderStatus(Long orderId) throws EntityNotFoundException;
}

package it.s8.awesomepizza.service;

import jakarta.persistence.EntityNotFoundException;
import org.openapitools.model.PizzaOrderToPay;

public interface PizzaOrderPaymentFacade {

  PizzaOrderToPay calculateOrderTotal(Long orderId) throws EntityNotFoundException;
}

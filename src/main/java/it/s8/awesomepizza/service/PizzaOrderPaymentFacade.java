package it.s8.awesomepizza.service;

import it.s8.awesomepizza.exception.OrderNotReadyException;
import jakarta.persistence.EntityNotFoundException;
import org.openapitools.model.PizzaOrderPaid;
import org.openapitools.model.PizzaOrderToPay;

public interface PizzaOrderPaymentFacade {

  PizzaOrderToPay calculateOrderTotal(Long orderId) throws EntityNotFoundException;

  void processPayment(Long orderId, PizzaOrderPaid pizzaOrderPaid) throws EntityNotFoundException, OrderNotReadyException;
}

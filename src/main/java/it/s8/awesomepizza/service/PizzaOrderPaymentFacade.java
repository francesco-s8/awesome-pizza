package it.s8.awesomepizza.service;

import it.s8.awesomepizza.exception.OrderNotReadyToBePaidException;
import jakarta.persistence.EntityNotFoundException;
import org.openapitools.model.PizzaOrderPaymentInfo;
import org.openapitools.model.PizzaOrderTotalToPay;

public interface PizzaOrderPaymentFacade {

  PizzaOrderTotalToPay calculateOrderTotal(Long orderId) throws EntityNotFoundException;

  void processPayment(Long orderId, PizzaOrderPaymentInfo pizzaOrderPaid) throws EntityNotFoundException, OrderNotReadyToBePaidException;
}

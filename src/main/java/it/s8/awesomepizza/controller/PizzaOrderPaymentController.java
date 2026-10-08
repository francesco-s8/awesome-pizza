package it.s8.awesomepizza.controller;

import it.s8.awesomepizza.service.PizzaOrderPaymentFacade;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaOrderPaid;
import org.openapitools.model.PizzaOrderToPay;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PizzaOrderPaymentController implements PaymentApi {

  final PizzaOrderPaymentFacade pizzaOrderPaymentFacade;

  public PizzaOrderPaymentController(PizzaOrderPaymentFacade pizzaOrderPaymentFacade) {
    this.pizzaOrderPaymentFacade = pizzaOrderPaymentFacade;
  }

  @Override
  public ResponseEntity<PizzaOrderToPay> _calculateOrderTotal(Long orderId) {
    var orderTotalPrice = pizzaOrderPaymentFacade.calculateOrderTotal(orderId);
    log.info("Calculated total price for order {} is : {}", orderId, orderTotalPrice);
    return ResponseEntity.ok(orderTotalPrice);
  }

  @Override
  public ResponseEntity<Void> _markOrderAsPaid(Long orderId, PizzaOrderPaid pizzaOrderPaid) {
    pizzaOrderPaymentFacade.processPayment(orderId, pizzaOrderPaid);
    return ResponseEntity.noContent().build();
  }
}

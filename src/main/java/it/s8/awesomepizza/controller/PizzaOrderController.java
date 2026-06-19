package it.s8.awesomepizza.controller;

import it.s8.awesomepizza.service.PizzaOrderFacade;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaOrderRequest;
import org.openapitools.model.PizzaOrderResponse;
import org.openapitools.model.PizzaOrderStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class PizzaOrderController implements OrderApi {

  final PizzaOrderFacade pizzaOrderFacade;

  public PizzaOrderController(PizzaOrderFacade pizzaOrderFacade) {
    this.pizzaOrderFacade = pizzaOrderFacade;
  }

  @Override
  public ResponseEntity<PizzaOrderStatus> _getOrderStatus(Long orderId) {
    var orderStatus = pizzaOrderFacade.retrieveOrderStatus(orderId);
    return ResponseEntity.ok(
        PizzaOrderStatus.builder()
            .orderStatus(PizzaOrderStatus.OrderStatusEnum.fromValue(orderStatus))
            .build());
  }

  @Override
  public ResponseEntity<PizzaOrderResponse> _newOrder(PizzaOrderRequest pizzaOrderRequest) {
    log.info("Received pizza order {}", pizzaOrderRequest);
    var order = pizzaOrderFacade.processOrder(pizzaOrderRequest);
    return ResponseEntity.ok(PizzaOrderResponse.builder().orderId(order.orderId()).build());
  }
}

package it.s8.awesomepizza.controller;

import it.s8.awesomepizza.dto.OrderDto;
import it.s8.awesomepizza.dto.OrderRequest;
import it.s8.awesomepizza.service.PizzaOrderFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/awesome-pizza")
public class PizzaOrderController {

  final PizzaOrderFacade pizzaOrderFacade;

  public PizzaOrderController(PizzaOrderFacade pizzaOrderFacade) {
    this.pizzaOrderFacade = pizzaOrderFacade;
  }

  @PostMapping("/order")
  public ResponseEntity<OrderDto> orderPizza(@RequestBody OrderRequest orderRequest) {

    log.info("Received pizza order {}", orderRequest);
    return ResponseEntity.ok(pizzaOrderFacade.processOrder(orderRequest));
  }

  @GetMapping("/order/{orderId}")
  public ResponseEntity<String> checkOrderStatus(@PathVariable Long orderId) {
    log.info("Checking status for order ID: {}", orderId);

    var orderStatus = pizzaOrderFacade.retrieveOrderStatus(orderId);
    return ResponseEntity.ok(orderStatus);
  }
}

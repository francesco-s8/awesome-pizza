package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.CurrencyUtils;
import it.s8.awesomepizza.entity.Pizza;
import it.s8.awesomepizza.service.PizzaOrderPaymentFacade;
import it.s8.awesomepizza.service.PizzaOrderService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.openapitools.model.PizzaOrderToPay;
import org.springframework.stereotype.Component;

@Component
public class PizzaOrderPaymentFacadeImpl implements PizzaOrderPaymentFacade {

  final PizzaOrderService pizzaOrderService;

  public PizzaOrderPaymentFacadeImpl(PizzaOrderService pizzaOrderService) {
    this.pizzaOrderService = pizzaOrderService;
  }

  @Override
  public PizzaOrderToPay calculateOrderTotal(Long orderId) throws EntityNotFoundException {
    var order = pizzaOrderService.getOrder(orderId);
    var total =
        order.getPizzaList().stream()
            .map(Pizza::getPrice)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP)
            .movePointRight(2)
            .longValue();
    return PizzaOrderToPay.builder()
        .name(order.getUsername())
        .total(CurrencyUtils.getTotalAsString(total))
        .build();
  }
}

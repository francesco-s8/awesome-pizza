package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.exception.OrderNotReadyException;
import it.s8.awesomepizza.service.PizzaOrderPaymentFacade;
import it.s8.awesomepizza.service.PizzaOrderService;
import it.s8.awesomepizza.utils.PizzaOrderUtils;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Objects;
import org.openapitools.model.PizzaOrderPaymentInfo;
import org.openapitools.model.PizzaOrderStatus;
import org.openapitools.model.PizzaOrderTotalToPay;
import org.springframework.stereotype.Component;

@Component
public class PizzaOrderPaymentFacadeImpl implements PizzaOrderPaymentFacade {

  final PizzaOrderService pizzaOrderService;

  public PizzaOrderPaymentFacadeImpl(PizzaOrderService pizzaOrderService) {
    this.pizzaOrderService = pizzaOrderService;
  }

  @Override
  public PizzaOrderTotalToPay calculateOrderTotal(Long orderId) throws EntityNotFoundException {
    var order = pizzaOrderService.getOrder(orderId);
    if (!Objects.equals(
        order.getOrderStatus(), PizzaOrderStatus.OrderStatusEnum.READY_FOR_DELIVERY.getValue())) {
      throw new AwesomePizzaException("Order " + orderId + " is not ready for delivery");
    }
    return PizzaOrderTotalToPay.builder()
        .name(order.getUsername())
        .order(order.getId())
        .total(PizzaOrderUtils.getTotalAsString(PizzaOrderUtils.getTotal(order).longValue()))
        .build();
  }

  @Override
  public void processPayment(Long orderId, PizzaOrderPaymentInfo pizzaOrderPaid)
      throws EntityNotFoundException, OrderNotReadyException {
    var order = pizzaOrderService.getOrder(orderId);
    if (Objects.equals(
        order.getOrderStatus(), PizzaOrderStatus.OrderStatusEnum.READY_FOR_DELIVERY.getValue())) {

      order.setPaid(true);
      order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.PAID.getValue());
      order.setPaymentMethod(pizzaOrderPaid.getPaymentMethod().getValue());
      order.setPaymentDate(Instant.now());
      pizzaOrderService.saveOrder(order);
      return;
    }
    throw new OrderNotReadyException(
        "Order " + orderId + " is not ready for delivery, cannot process payment");
  }
}

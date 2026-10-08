package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.exception.OrderNotReadyException;
import it.s8.awesomepizza.service.PizzaOrderPaymentFacade;
import it.s8.awesomepizza.service.PizzaOrderService;
import it.s8.awesomepizza.utils.PizzaOrderUtils;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaOrderPaymentInfo;
import org.openapitools.model.PizzaOrderStatus;
import org.openapitools.model.PizzaOrderTotalToPay;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PizzaOrderPaymentFacadeImpl implements PizzaOrderPaymentFacade {

  final PizzaOrderService pizzaOrderService;

  public PizzaOrderPaymentFacadeImpl(PizzaOrderService pizzaOrderService) {
    this.pizzaOrderService = pizzaOrderService;
  }

  @Override
  public PizzaOrderTotalToPay calculateOrderTotal(Long orderId) throws EntityNotFoundException {
    var order = pizzaOrderService.getOrder(orderId);
    if (Objects.isNull(order.getTotal())
        && !Objects.equals(
            order.getOrderStatus(),
            PizzaOrderStatus.OrderStatusEnum.READY_FOR_DELIVERY.getValue())) {
      throw new AwesomePizzaException("Order " + orderId + " is not ready for payment");
    }
    if (Objects.equals(
        order.getOrderStatus(), PizzaOrderStatus.OrderStatusEnum.TO_BE_PAID.getValue())) {
      log.info("Order {} is ready to be paid", orderId);
      throw new AwesomePizzaException("Order " + orderId + " is already marked as TO_BE_PAID");
    }
    var total = PizzaOrderUtils.getTotal(order).longValue();
    order.setTotal(BigDecimal.valueOf(total));
    order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.TO_BE_PAID.getValue());
    pizzaOrderService.saveOrder(order);

    return PizzaOrderTotalToPay.builder()
        .name(order.getUsername())
        .order(order.getId())
        .total(PizzaOrderUtils.getTotalAsString(total))
        .build();
  }

  @Override
  public void processPayment(Long orderId, PizzaOrderPaymentInfo pizzaOrderPaid)
      throws EntityNotFoundException, OrderNotReadyException {
    var order = pizzaOrderService.getOrder(orderId);
    if (Objects.equals(
        order.getOrderStatus(), PizzaOrderStatus.OrderStatusEnum.TO_BE_PAID.getValue())) {

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

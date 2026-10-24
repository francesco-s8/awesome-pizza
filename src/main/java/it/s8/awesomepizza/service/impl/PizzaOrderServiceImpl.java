package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.exception.AwesomePizzaException;
import it.s8.awesomepizza.exception.AwesomePizzaOperationNotSupportedException;
import it.s8.awesomepizza.repository.PizzaOrderRepository;
import it.s8.awesomepizza.service.PizzaOrderService;
import it.s8.awesomepizza.utils.PizzaOrderUtils;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PizzaOrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PizzaOrderServiceImpl implements PizzaOrderService {

  private final PizzaOrderRepository pizzaOrderRepository;

  public PizzaOrderServiceImpl(PizzaOrderRepository pizzaOrderRepository) {
    this.pizzaOrderRepository = pizzaOrderRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public PizzaOrder getOrder(Long orderId) {
    var orderFound =
        pizzaOrderRepository
            .findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order " + orderId + " not found"));
    log.info(
        "Order found with status {} with pizzas size {}",
        orderFound.getOrderStatus(),
        orderFound.getPizzaList().size());
    return orderFound;
  }

  @Override
  @Transactional
  public PizzaOrder saveOrder(PizzaOrder pizzaOrder) {

    var order = pizzaOrderRepository.save(pizzaOrder);
    log.info("Order from customer {} saved with id {}", order.getUsername(), order.getId());
    return order;
  }

  @Transactional
  @Override
  public void prepareOrder(Long pizzaOrderId) {
    PizzaOrder order =
        pizzaOrderRepository
            .findById(pizzaOrderId)
            .orElseThrow(
                () -> new EntityNotFoundException("Order not found with ID: " + pizzaOrderId));

    var total = PizzaOrderUtils.getTotal(order);
    if (total.compareTo(BigDecimal.ZERO) == 0) {
      log.warn("Order {} total is zero, recalculating", pizzaOrderId);
      throw new AwesomePizzaException("Order total is not correct, please recalculate the order");
    }

    order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.READY_FOR_DELIVERY.getValue());
    order.setTotal(total);
    pizzaOrderRepository.save(order);
  }

  @Override
  public void deleteOrder(Long orderId) throws AwesomePizzaOperationNotSupportedException {
    var order =
        pizzaOrderRepository
            .findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

    if (!PizzaOrderUtils.orderCanBeDeleted(order)) {

      throw new AwesomePizzaOperationNotSupportedException(
          "Order "
              + order.getId()
              + " cannot be cancelled its status is "
              + order.getOrderStatus());
    }
    order.setOrderStatus(PizzaOrderStatus.OrderStatusEnum.CANCELLED.getValue());
    pizzaOrderRepository.save(order);
  }
}

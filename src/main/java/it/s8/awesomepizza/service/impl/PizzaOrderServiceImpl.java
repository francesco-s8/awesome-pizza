package it.s8.awesomepizza.service.impl;

import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.repository.PizzaOrderRepository;
import it.s8.awesomepizza.service.PizzaOrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
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
  @Transactional
  public PizzaOrder getOrderStatus(Long orderId) {
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
    log.info("Order {} saved with id {}", order.getUsername(), order.getId());
    return order;
  }
}

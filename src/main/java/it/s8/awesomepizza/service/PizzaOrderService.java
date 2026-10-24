package it.s8.awesomepizza.service;

import it.s8.awesomepizza.entity.PizzaOrder;
import it.s8.awesomepizza.exception.AwesomePizzaOperationNotSupportedException;

public interface PizzaOrderService {

    PizzaOrder getOrder(Long orderId);

    PizzaOrder saveOrder(PizzaOrder pizzaOrder);

    void prepareOrder(Long pizzaOrderId);

    void deleteOrder(Long orderId) throws AwesomePizzaOperationNotSupportedException;
}

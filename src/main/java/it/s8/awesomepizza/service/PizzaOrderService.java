package it.s8.awesomepizza.service;

import it.s8.awesomepizza.entity.PizzaOrder;

public interface PizzaOrderService {

    PizzaOrder getOrderStatus(Long orderId);

    PizzaOrder saveOrder(PizzaOrder pizzaOrder);
}

package it.s8.awesomepizza.dto;

import it.s8.awesomepizza.entity.Pizza;
import java.util.List;

public record PizzaOrderDto(String user, List<Pizza> pizzaList, String orderStatus) {
}

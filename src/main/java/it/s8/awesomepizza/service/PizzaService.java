package it.s8.awesomepizza.service;

import it.s8.awesomepizza.entity.Pizza;

import java.util.List;

public interface PizzaService {

    List<Pizza> getPizzas(List<String> pizzaNames);

}
